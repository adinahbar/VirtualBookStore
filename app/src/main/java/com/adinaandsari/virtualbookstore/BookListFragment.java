package com.adinaandsari.virtualbookstore;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Book> bookArrayList;
    private List<BookItemForList> bookItemForLists;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookListFragment newInstance(String param1, String param2) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_book, container, false);
        Intent i = getActivity().getIntent();
        bookArrayList = (ArrayList<Book>)i.getSerializableExtra(ConstValue.BOOK_LIST_KEY);
        bookItemForLists = new ArrayList<>();
        for(Book b : bookArrayList)
        {
            bookItemForLists.add(new BookItemForList(b.getBookName(),b.getBookID(),b.getAuthor()));
        }
        ListView listView = (ListView)getActivity().findViewById(R.id.listView__of_book);
        ArrayAdapter<BookItemForList> adapter = new ArrayAdapter<BookItemForList>(getActivity(),R.layout.row_of_book_item,bookItemForLists)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(getActivity(), R.layout.row_of_book_item,null);
                }
                TextView bookID = (TextView)convertView.findViewById(R.id.bookIdTextView_row);
                TextView bookName = (TextView)convertView.findViewById(R.id.bookTitleTextVieww_row);
                TextView bookAuthor = (TextView)convertView.findViewById(R.id.bookAuthorTextVieww_row);
                bookID.setText(String.valueOf(bookItemForLists.get(position).getId()));
                bookName.setText(bookItemForLists.get(position).getName());
                bookAuthor.setText(bookItemForLists.get(position).getAuthor());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        getActivity().setContentView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View item, int position, long id) {
                Intent intent = new Intent(getActivity(), BookPage.class);
                BookItemForList bookItemForList = (BookItemForList)listView.getSelectedItem();
                try {
                    Book b = BackendFactory.getInstance().findBookByID(bookItemForList.getId());
                    intent.putExtra(ConstValue.SUPPLIER_KEY, b);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(getActivity(), "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        adapter.notifyDataSetChanged();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
