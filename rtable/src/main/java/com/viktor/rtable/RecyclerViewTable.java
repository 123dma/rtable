package com.viktor.rtable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.viktor.rtable.components.ColumnHeader;
import com.viktor.rtable.components.FixedGridLayoutManager;
import com.viktor.rtable.components.RTableAdapter;
import com.viktor.rtable.events.OnRowClicked;
import com.viktor.rtable.utils.Pagination;
import com.viktor.rtable.utils.Utils;

import java.util.List;

/**
 *
 * Component which uses a RecyclerView, ScrollView (header) and layout with footer.
 *
 * RecyclerView renders the information, the scrollview auto scrolls to the correspondent
 * position of the elements that are been showed, footer contains arrows to move between pages.
 *
 *
 * HOW TO USE:
 *
 *
 * Object can be used as simple layout in this way:
 *
     <com.viktor.rtable.RecyclerViewTable
         android:id="@+id/grid_recycler"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         />
 *
 * After that in the activity write ur configure method:
 *
 *         recyclerTable = findViewById(R.id.grid_recycler);
 *         recyclerTable.configure(
                 new ColumnHeader[]{
                          new ColumnHeader(50, "id"),
                          new ColumnHeader(200, "name"),
                          new ColumnHeader(300, "address"),
                  }
                  , (List) setupCollection(), MyJavaBean.class, new OnRowClicked() {
                      @Override
                      public void onClick(View view, int rowIndex) {
                          Log.d("INFO", "rowIndex: "+rowIndex);
                      }
                  })
 *
 *
 *  OnRowClicked is optional.
 *
 *  MyJavaBean.class should be mapped in the following way:
 *
         public class MyJavaBean {
            @GridColumn(width = 50, position = 0)
            private String id;
            @GridColumn(width = 200, position = 1)
            private String name;
            @GridColumn(width = 300, position = 2)
            private String address;
 *
 * width will be transform to dp units, positon is the respect position in the table, starts from 0.
 *
 */
public class RecyclerViewTable extends LinearLayout {
    private RecyclerView recyclerDetail;
    private HorizontalScrollView scrollView;
    private Pagination<Object> pagination;
    private RTableAdapter adapter;
    private int scrollX = 0;
    private Class clazz;
    private ColumnHeader[] headers;
    private OnRowClicked onRowClicked;
    private TextView textViewNoRows;
    private int quantidadePaginas;
    public int indexAtual;
    public int txtColor;
    private  ImageView botaoVoltar;
    private ImageView botaoAvancar;

    public RecyclerViewTable(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx){
        init(ctx, null);
    }

    private void init(Context ctx, AttributeSet attrs){
        inflate(ctx, R.layout.layout_rtable_component, this);
        this.textViewNoRows = findViewById(R.id.rtable_no_data_msg);
        this.recyclerDetail = findViewById(R.id.grid_recycler_content);

        this.botaoVoltar = findViewById(R.id.click_dtable_before_);
        this.botaoAvancar = findViewById(R.id.click_dtable_after);
    }

    private void setUpRecyclerView(int txtColor){
        FixedGridLayoutManager gridLayoutManager = new FixedGridLayoutManager();
        gridLayoutManager.setTotalColumnCount(1);

        if(txtColor == 1)
        {
            botaoAvancar.setColorFilter(getResources().getColor(R.color.rtable_header_textColorBlack));
            botaoVoltar.setColorFilter(getResources().getColor(R.color.rtable_header_textColorBlack));
        }

        if(onRowClicked == null){
            adapter = new RTableAdapter(getContext(), pagination.pages(), clazz, txtColor);
        }else{
            adapter = new RTableAdapter(getContext(), pagination.pages(), clazz, onRowClicked, pagination.getPageIndex(),txtColor);
        }

       

        recyclerDetail.setLayoutManager(gridLayoutManager);
        recyclerDetail.setAdapter(adapter);
        recyclerDetail.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollX += dx;
                scrollView.scrollTo(scrollX, 0);
            }
        });


        botaoVoltar.setOnClickListener(beforeEvent);
        botaoAvancar.setOnClickListener(afterEvent);
    }

    public int obterIndex(){
        return indexAtual;
    }


    private View.OnClickListener beforeEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            voltarPagina(view);
        }
    };
    private void voltarPagina(View view){

        int pageIndex = pagination.getPageIndex()-1;
        indexAtual = pageIndex;
        List<Object> list = pagination.prev();

        if(onRowClicked == null){
            adapter = new RTableAdapter(getContext(), list, clazz, txtColor);
        }else{
            adapter = new RTableAdapter(getContext(), list, clazz, onRowClicked, pageIndex, txtColor);
        }

        try {
            TextView myAwesomeTextView = (TextView) findViewById(R.id.pagina_atual);
            myAwesomeTextView.setText(String.valueOf(pageIndex+1));

             if (pageIndex == 0) {
                view.setEnabled(false);
            } else {
                view.setEnabled(true); }

           if (quantidadePaginas < (pageIndex + 1)) {
                botaoAvancar.setEnabled(false);
            } else {
                botaoAvancar.setEnabled(true);
            }

        } catch (Exception ex) {

        }

        recyclerDetail.swapAdapter(adapter, false);
        scrollX = 0;
        scrollView.scrollTo(0,0);

    }

    private void avancarPagina(View view){

        int pageIndex = pagination.getPageIndex()+1;
        indexAtual = pageIndex;
        List<Object> list = pagination.next(pageIndex);

        if(onRowClicked == null){
            adapter = new RTableAdapter(getContext(), list, clazz, txtColor);
        }else{
            adapter = new RTableAdapter(getContext(), list, clazz, onRowClicked, pageIndex, txtColor);
        }
        try {
           if (quantidadePaginas ==  (pageIndex+1)) {
                view.setEnabled(false);
            }else {
                view.setEnabled(true);
            }

            if (pageIndex == 0){
                botaoVoltar.setEnabled(false);
            }else{
                botaoVoltar.setEnabled(true);
            }
            TextView myAwesomeTextView = (TextView)findViewById(R.id.pagina_atual);
            myAwesomeTextView.setText(String.valueOf(pageIndex+1));


        } catch (Exception ex) {

        }
        recyclerDetail.swapAdapter(adapter, false);
        scrollX = 0;
        scrollView.scrollTo(0,0);
    }



    private View.OnClickListener afterEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            avancarPagina(view);
        }
    };

    /**
     *
     * @param collection
     * @param clazz
     */
    public void configure(ColumnHeader[] headers, List<Object> collection, Class clazz, int indexPage, int textColor){
        if(!collection.isEmpty()){
            setupGridHeader(headers, textColor);
            this.clazz = clazz;
            pagination = new Pagination<>(collection, getResources().getInteger(R.integer.rtable_rowsPerPage), indexPage);



            setUpRecyclerView(txtColor);
            showRecycler();
        }
    }
 
 
    /**
     *
     * @param collection
     * @param clazz
     */
    public void limpar(){
        recyclerDetail.setVisibility(GONE);
        textViewNoRows.setVisibility(VISIBLE);
    }

 
    /**
     *
     * @param collection
     * @param clazz
     */
    public void configure(ColumnHeader[] headers, List<Object> collection, Class clazz, OnRowClicked onRowClicked, int indexPage, int textColor){
        if(!collection.isEmpty()){
            setupGridHeader(headers, textColor);
            this.clazz = clazz;
            this.onRowClicked = onRowClicked;
            pagination = new Pagination<>(collection, getResources().getInteger(R.integer.rtable_rowsPerPage), indexPage);
            try {
                if(collection.size() > 0){
                    TextView myAwesomeTextView = (TextView)findViewById(R.id.total_paginas);
                    quantidadePaginas = (collection.size() / pagination.itemsPerPage)+1;
                    myAwesomeTextView.setText(String.valueOf(quantidadePaginas));
                }
            } catch (Exception ex) {

            }
           // if(indexPage != 0){
             //   avancarPagina(this);
           // }
            TextView myAwesomeTextView = (TextView)findViewById(R.id.pagina_atual);
            myAwesomeTextView.setText(String.valueOf(indexPage+1));

            indexAtual = indexPage;
            txtColor = textColor;
            setUpRecyclerView(txtColor);
            showRecycler();
        }
    }

    private void setupGridHeader(ColumnHeader[] headers, int textColor) {
        this.headers = headers;
        scrollView = findViewById(R.id.grid_recycler_header);

        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.scrollv_header_detail_emp, scrollView, false);
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        view.setGravity(Gravity.BOTTOM);

        int i = 0;

        while (i < this.headers.length){
            view.addView(createTextView(headers[i],textColor));
            view.setGravity(Gravity.CENTER_HORIZONTAL);
            i++;
        }

        scrollView.addView(view);
    }

    private TextView createTextView(ColumnHeader header, int textColor) {
        TextView txt = new TextView(getContext());
        txt.setId(Utils.randomInt());
        txt.setText(header.getHeaderText());
        if(textColor == 1)
            txt.setTextColor(getResources().getColor(R.color.rtable_header_textColorBlack));
        else
            txt.setTextColor(getResources().getColor(R.color.rtable_header_textColor));

        txt.setLayoutParams(new ViewGroup.LayoutParams(
                Utils.Conversion.dp(header.getWidth(), getResources()),
                    ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return txt;
    }

    private void showRecycler(){
        recyclerDetail.setVisibility(VISIBLE);
        textViewNoRows.setVisibility(GONE);
    }
/*    private View.OnClickListener beforeEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            List<Object> list = pagination.prev();

            if(onRowClicked == null){
                adapter = new RTableAdapter(getContext(), list, clazz);
            }else{
                adapter = new RTableAdapter(getContext(), list, clazz, onRowClicked, pagination.getPageIndex());
            }

            try {
                TextView myAwesomeTextView = (TextView) findViewById(R.id.pagina_atual);
                myAwesomeTextView.setText(String.valueOf(pagination.getPageIndex() + 1));

                if (pagination.getPageIndex() == 0) {
                    view.setEnabled(false);
                } else {
                    view.setEnabled(true); }

                if (quantidadePaginas < (pagination.getPageIndex() + 1)) {
                    botaoAvancar.setEnabled(false);
                } else {
                    botaoAvancar.setEnabled(true);
                }

            } catch (Exception ex) {

            }




            recyclerDetail.swapAdapter(adapter, false);
            scrollX = 0;
            scrollView.scrollTo(0,0);
        }
    };




    private View.OnClickListener afterEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            List<Object> list = pagination.next();

            if(onRowClicked == null){
                adapter = new RTableAdapter(getContext(), list, clazz);
            }else{
                adapter = new RTableAdapter(getContext(), list, clazz, onRowClicked, pagination.getPageIndex());
            }


            try {
                if (quantidadePaginas ==  (pagination.getPageIndex()+1)) {
                    view.setEnabled(false);
                }else {
                    view.setEnabled(true);
                }

                if (pagination.getPageIndex() == 0){
                    botaoVoltar.setEnabled(false);
                }else{
                    botaoVoltar.setEnabled(true);
                }

                TextView myAwesomeTextView = (TextView)findViewById(R.id.pagina_atual);
                myAwesomeTextView.setText(String.valueOf(pagination.getPageIndex()+1));
            } catch (Exception ex) {

            }





            recyclerDetail.swapAdapter(adapter, false);
            scrollX = 0;
            scrollView.scrollTo(0,0);
        }
    };*/
}
