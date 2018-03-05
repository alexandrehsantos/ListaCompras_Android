package br.com.alexandre.listacompras;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.alexandre.listacompras.model.Item;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText edtDescricao, edtQuantidade;


    ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Item> listItem = new ArrayList<>();

    private ArrayAdapter<Item> arrayAdapterPessoa;

    Item itemSelecionada;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtQuantidade = (EditText)findViewById(R.id.edtQuantidade);
        edtDescricao = (EditText)findViewById(R.id.editDescricao);
        listV_dados = (ListView) findViewById(R.id.listV_dados);

        inicializarFirebase();
        eventoDatabase();


        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelecionada = (Item) parent.getItemAtPosition(position);
                edtDescricao.setText(itemSelecionada.getDescricao());
                edtQuantidade.setText(itemSelecionada.getQuantidade().toString());

            }
        });


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Bem vindo de volta " + user.getEmail() + "!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novoItem();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void novoItem() {
        Item p = new Item();
        p.setuId(UUID.randomUUID().toString());
        p.setDescricao(edtDescricao.getText().toString());
        p.setQuantidade(Double.valueOf(edtQuantidade.getText().toString()));
        databaseReference.child("FolhaPonto").child(p.getuId()).setValue(p);
        limparCampos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
//        else if(id == R.id.menu_novo){
//            Item p = new Item();
//            p.setuId(UUID.randomUUID().toString());
//            p.setDescricao(edtDescricao.getText().toString());
//            p.setQuantidade(Double.valueOf(edtQuantidade.getText().toString()));
//            databaseReference.child("FolhaPonto").child(p.getuId()).setValue(p);
//            limparCampos();
//        }
        else if(id == R.id.menu_atualiza){
            Item p = new Item();
            p.setuId(itemSelecionada.getuId());
            p.setDescricao(edtDescricao.getText().toString().trim());
            p.setQuantidade(Double.valueOf(edtQuantidade.getText().toString()));
            databaseReference.child("FolhaPonto").child(p.getuId()).setValue(p);
            limparCampos();
        } else if (id == R.id.menu_deleta){
            Item p = new Item();
            p.setuId(itemSelecionada.getuId());
            databaseReference.child("FolhaPonto").child(p.getuId()).removeValue();
            limparCampos();
        }
        //TODO return true
        return super.onOptionsItemSelected(item);
    }

    private void eventoDatabase() {
        databaseReference.child("FolhaPonto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItem.clear();
                for(DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Item p = objSnapshot.getValue(Item.class);
                    listItem.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Item>(MainActivity.this, android.R.layout.simple_list_item_1, listItem);

                listV_dados.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void  inicializarFirebase(){
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase = Utils.getDatabase();
        databaseReference = firebaseDatabase.getReference();
    }

    private void limparCampos() {
        edtQuantidade.setText("");
        edtDescricao.setText("");
    }



}
