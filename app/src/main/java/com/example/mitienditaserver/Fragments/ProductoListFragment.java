package com.example.mitienditaserver.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitienditaserver.Common.Common;
import com.example.mitienditaserver.Interface.IItemClickListener;
import com.example.mitienditaserver.Model.ProductoModel;
import com.example.mitienditaserver.Model.PromocionModel;
import com.example.mitienditaserver.R;
import com.example.mitienditaserver.ViewModel.ProductoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class ProductoListFragment extends Fragment {

    //Declaracion de variables
    private FrameLayout flPlatilloLista;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FloatingActionButton btnAddFood;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView lblCategoryID;
    private String categoryID = "";

    private RecyclerView rvPlatillo;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<ProductoModel, ProductoViewHolder> adapter;

    private FirebaseRecyclerAdapter<ProductoModel, ProductoViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    private MaterialSearchBar searchBar;

    //Agregar layout de menu
    private MaterialEditText edtFoodName;
    private MaterialEditText edtFoodDescription;
    private MaterialEditText edtFoodPrice;
    private MaterialEditText edtFoodDiscount;
    private MaterialEditText edtFoodStatus;
    private FButton btnSelectFood;
    private FButton btnUploadFood;

    ProductoModel platilloModel;
    Uri saveUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = this.getArguments();
        if (data != null) {
            categoryID = data.getString("categoryID");
        }//if
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_producto_list, container, false);

        flPlatilloLista = view.findViewById(R.id.flPlatilloLista);
        searchBar = view.findViewById(R.id.searchBar);
        rvPlatillo = view.findViewById(R.id.rvPlatillo);

        //Conexion e instanciacion a  firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Food");
        btnAddFood = view.findViewById(R.id.fbAddFood);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("images/");


        //Cargar platillos
        rvPlatillo.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvPlatillo.setLayoutManager(layoutManager);

        cargarProducto(categoryID);

        //Search
        searchBar.setHint("Inserta el producto");

        //loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }//beforeTextChanged

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }//if
                }//for
            }//onTextChanged

            @Override
            public void afterTextChanged(Editable editable) {

            }//afterTextChanged
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Restauramos el adaptador cuando la busqueda se cierra
                if (!enabled) {
                    rvPlatillo.setAdapter(adapter);
                }//if
            }//onSearchStateChanged

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Mostramos resultado cuando la busqueda termina
                startSearch(text);
            }//onSearchConfirmed

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFrament(new AgregarProductoFragment());
                //showDialog();
            }//onClick
        });

        return view;
    }//onCreateView


    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<ProductoModel, ProductoViewHolder>(
                ProductoModel.class,
                R.layout.product_item,
                ProductoViewHolder.class,
                databaseReference.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(ProductoViewHolder productoViewHolder, ProductoModel productoModel, int i) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento...");
                progressDialog.show();

                productoViewHolder.lblFood_title.setText(platilloModel.getName());
                Picasso.with(getActivity().getBaseContext()).load(platilloModel.getImage())
                        .into(productoViewHolder.lblFood_image);
                productoViewHolder.lblFood_price.setText(platilloModel.getPrice());
                productoViewHolder.lblFood_discount.setText(platilloModel.getDiscount());
                productoViewHolder.lblFood_status.setText(platilloModel.getStatus());

                progressDialog.dismiss();

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
                productoViewHolder.lblFood_title.setTypeface(typeface);

                final ProductoModel producto = productoModel;
                productoViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getActivity(),"¡Producto seleccionado!",Toast.LENGTH_LONG).show();
                    }//onClick
                });
            }//populateViewHolder
        };
        rvPlatillo.setAdapter(searchAdapter);
    }//startSearch


    private void loadSuggest() {
        databaseReference.orderByChild("menuID").equalTo(categoryID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ProductoModel item = postSnapshot.getValue(ProductoModel.class);
                            suggestList.add(item.getName());
                        }//for
                    }//onDataChange

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled
                });
    }//loadSuggest


    private void cargarProducto(String categoryID) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere un momento...");
        progressDialog.show();

        //Equivalete a select * from food where MenuID =
        adapter = new FirebaseRecyclerAdapter<ProductoModel, ProductoViewHolder>(ProductoModel.class, R.layout.product_item, ProductoViewHolder.class, databaseReference.orderByChild("menuID").equalTo(categoryID)) {
            @Override
            protected void populateViewHolder(ProductoViewHolder platilloViewHolder, ProductoModel platilloModel, int i) {
                platilloViewHolder.lblFood_title.setText(platilloModel.getName());
                Picasso.with(getActivity().getBaseContext()).load(platilloModel.getImage())
                        .into(platilloViewHolder.lblFood_image);
                platilloViewHolder.lblFood_price.setText(platilloModel.getPrice());
                platilloViewHolder.lblFood_discount.setText(platilloModel.getDiscount());
                platilloViewHolder.lblFood_status.setText(platilloModel.getStatus());

                progressDialog.dismiss();

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
                platilloViewHolder.lblFood_title.setTypeface(typeface);

                final ProductoModel platillos = platilloModel;
                platilloViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Toast.makeText(getActivity(),"¡Producto seleccionado!",Toast.LENGTH_LONG).show();

                    }//onClick
                });
            }//populateViewHolder
        };
        rvPlatillo.setAdapter(adapter);
    }//cargarProducto

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            //showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            Toast.makeText(getActivity(),"Actualizacion seleccionada",Toast.LENGTH_LONG).show();
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteProduct(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }//else

        return super.onContextItemSelected(item);
    }//onContextItemSelected

    private void deleteProduct(String key, ProductoModel item) {

        //Eliminar categoria con productos dentro de la misma
        DatabaseReference databaseReference = firebaseDatabase.getReference("Promotion");
        Query foodInCategory = databaseReference.orderByChild("menuID").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    postSnapShot.getRef().removeValue();
                }//for
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled
        });

        databaseReference.child(key).removeValue();
        Toast.makeText(getActivity(), "¡Elemento eliminado!", Toast.LENGTH_LONG).show();
    }//deletePromotion

    //Llamo fragment de menu de productos
    private void setFrament(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(flPlatilloLista.getId(), fragment);
        fragmentTransaction.commit();
    }//setFrament

}//ProductoListFragment