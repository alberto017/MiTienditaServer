package com.example.mitienditaserver.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitienditaserver.Common.Common;
import com.example.mitienditaserver.Interface.IItemClickListener;
import com.example.mitienditaserver.Model.CategoriaModel;
import com.example.mitienditaserver.R;
import com.example.mitienditaserver.ViewModel.CategoriaViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class CategoriaListaFragment extends Fragment {

    //Declaracion de variables
    private FrameLayout flCategoriaLista;
    private TextView lblTitle_Categorias;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FloatingActionButton btnAddCategory;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private RecyclerView rvCategoria;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<CategoriaModel, CategoriaViewHolder> adapter;

    //Agregar layout de menu
    private MaterialEditText edtCategoryName;
    private SwitchMaterial swtCategoryStatus;
    private FButton btnSelectCategory;
    private FButton btnUploadCategory;

    CategoriaModel categoriaModel;
    Uri saveUri;
    ArrayAdapter arrayAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categoria_lista, container, false);

        //Enlazamos los componente
        flCategoriaLista = view.findViewById(R.id.flCategoriaLista);
        lblTitle_Categorias = view.findViewById(R.id.lblTitle_Categorias);
        rvCategoria = view.findViewById(R.id.rvCategoria);
        btnAddCategory = view.findViewById(R.id.fbAddCategory);

        //Conexion e instanciacion a  firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Category");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("images/");

        //Cargar categorias
        rvCategoria.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvCategoria.setLayoutManager(layoutManager);
        cargarCategoria();

        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
        lblTitle_Categorias.setTypeface(typeface);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }//onClick
        });

        return view;
    }//onCreateView

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("AGREGAR NUEVA CATEGORIA");
        alertDialog.setMessage("¡Introduce los datos por favor!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu = inflater.inflate(R.layout.add_new_category, null);
        edtCategoryName = add_new_menu.findViewById(R.id.edtCategoryName);
        btnSelectCategory = add_new_menu.findViewById(R.id.btnSelectCategory);
        btnUploadCategory = add_new_menu.findViewById(R.id.btnUploadCategory);

        btnSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }//onClick
        });

        btnUploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }//onClick
        });

        alertDialog.setView(add_new_menu);
        alertDialog.setIcon(R.drawable.cart);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (categoriaModel != null) {
                    databaseReference.push().setValue(categoriaModel);
                }//if
            }//onClick
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }//onClick
        });
        alertDialog.show();
    }//showDialog

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Subiendo...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "¡Carga finalizada!", Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    categoriaModel = new CategoriaModel(edtCategoryName.getText().toString(), uri.toString());
                                }//onSuccess
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }//onFailure
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Carga finalizada" + progress + "%");
                        }//onProgress
                    });
        }
    }//uploadImage

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelectCategory.setText("¡Imagen seleccionada!");
        }//if
    }//onActivityResult

    private void chooseImage() {
        Intent select = new Intent();
        select.setType("image/*");
        select.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(select, "Selecciona imagen"), Common.PICK_IMAGE_REQUEST);
    }//chooseImage

    private void cargarCategoria() {

        //Iniciamos nuestro progressDialog
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere un momento...");
        progressDialog.show();

        adapter = new FirebaseRecyclerAdapter<CategoriaModel, CategoriaViewHolder>(CategoriaModel.class, R.layout.category_item, CategoriaViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(CategoriaViewHolder menuViewHolder, CategoriaModel categoriaModel, int i) {
                menuViewHolder.lblCategory_title.setText(categoriaModel.getName());
                Picasso.with(getActivity().getBaseContext()).load(categoriaModel.getImage())
                        .into(menuViewHolder.lblCategory_image);

                //Detenemos el progressDialog por la carga de datos
                progressDialog.dismiss();

                //Cambiamos el chequeo de la promocion
                /*
                if (categoriaModel.getStatus().equals("Disponible")) {
                    swtCategoryStatus.isChecked();
                }//if
                 */

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
                menuViewHolder.lblCategory_title.setTypeface(typeface);

                final CategoriaModel clickItem = categoriaModel;

                menuViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClik) {

                        ProductoListFragment productoListFragment = new ProductoListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("categoryID", adapter.getRef(position).getKey());
                        productoListFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(flCategoriaLista.getId(), productoListFragment);
                        fragmentTransaction.commit();

                    }//onClick
                });
            }//populateViewHolder
        };
        rvCategoria.setAdapter(adapter);
    }//cargarCategoria

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteCategory(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }//else

        return super.onContextItemSelected(item);
    }//onContextItemSelected

    private void deleteCategory(String key, CategoriaModel item) {

        //Eliminar categoria con productos dentro de la misma
        DatabaseReference databaseReference = firebaseDatabase.getReference("Category");
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
    }//deleteCategory

    private void showUpdateDialog(final String key, final CategoriaModel item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("ACTUALIZAR CATEGORIA");
        alertDialog.setMessage("¡Introduce los datos por favor!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu = inflater.inflate(R.layout.add_new_category, null);
        edtCategoryName = add_new_menu.findViewById(R.id.edtCategoryName);
        swtCategoryStatus = add_new_menu.findViewById(R.id.swtCategoryStatus);
        btnSelectCategory = add_new_menu.findViewById(R.id.btnSelectCategory);
        btnUploadCategory = add_new_menu.findViewById(R.id.btnUploadCategory);

        //Asignar nombre por default
        edtCategoryName.setText(item.getName());

        btnSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }//onClick
        });

        btnUploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }//onClick
        });

        alertDialog.setView(add_new_menu);
        alertDialog.setIcon(R.drawable.cart);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setName(edtCategoryName.getText().toString());
                if (swtCategoryStatus.isChecked()) {
                    item.setStatus("Disponible");
                } else {
                    item.setStatus("No disponible");
                }//else
                databaseReference.child(key).setValue(item);
            }//onClick
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }//onClick
        });
        alertDialog.show();
    }//showUpdateDialog

    private void changeImage(final CategoriaModel item) {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Subiendo...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "¡Carga finalizada!", Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }//onSuccess
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }//onFailure
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Carga finalizada" + progress + "%");
                        }//onProgress
                    });
        }//if
    }//changeImage

}//CategoriaListaFragment