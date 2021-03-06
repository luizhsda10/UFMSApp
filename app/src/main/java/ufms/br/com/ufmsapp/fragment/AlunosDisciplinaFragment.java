/*
 * Copyright [2016] [UFMS]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ufms.br.com.ufmsapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import ufms.br.com.ufmsapp.MyApplication;
import ufms.br.com.ufmsapp.R;
import ufms.br.com.ufmsapp.adapter.AlunosAdapter;
import ufms.br.com.ufmsapp.pojo.AlunoXDisciplina;
import ufms.br.com.ufmsapp.pojo.Disciplina;
import ufms.br.com.ufmsapp.utils.Constants;
import ufms.br.com.ufmsapp.utils.OrientationUtils;


public class AlunosDisciplinaFragment extends Fragment {

    private RecyclerView mRecyclerAlunos;
    private AlunosAdapter adapter;
    protected Disciplina disciplina;
    ArrayList<AlunoXDisciplina> matriculas;
    CircularProgressBar progressBar;
    TextView emptyListText;
    ImageView emptyListImg;


    public AlunosDisciplinaFragment() {
    }

    public static AlunosDisciplinaFragment newInstance() {
        return new AlunosDisciplinaFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_alunos_disciplina, container, false);


        //disciplina = getActivity().getIntent().getParcelableExtra(DisciplinasFragment.DISCIPLINA_EXTRA);

        int disciplinaId = -1;
        if (getActivity().getIntent().getStringExtra(Constants.DISCIPLINA_CREATED_EXTRA) != null) {
            disciplinaId = Integer.parseInt(getActivity().getIntent().getStringExtra(Constants.DISCIPLINA_CREATED_EXTRA));
        }

        if (getActivity().getIntent().getParcelableExtra(DisciplinasFragment.DISCIPLINA_EXTRA) != null) {
            disciplina = getActivity().getIntent().getParcelableExtra(DisciplinasFragment.DISCIPLINA_EXTRA);
        } else {
            disciplina = MyApplication.getWritableDatabase().disciplinaById(disciplinaId);
        }

        mRecyclerAlunos = (RecyclerView) view.findViewById(R.id.recycler_alunos_disciplina);

        if (OrientationUtils.isPortrait(getResources().getConfiguration())) {
            mRecyclerAlunos.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            mRecyclerAlunos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

        progressBar = (CircularProgressBar) view.findViewById(R.id.progress_bar_list_alunos);

        emptyListText = (TextView) view.findViewById(R.id.aluno_txt_empty_text);
        emptyListImg = (ImageView) view.findViewById(R.id.aluno_empty_icon);

        adapter = new AlunosAdapter(getActivity());

        matriculas = MyApplication.getWritableDatabase().listMatriculas(disciplina.getIdDisciplinaServidor());

        if (matriculas != null) {
            adapter.setAlunosList(matriculas);
        }


        updateList();

        return view;
    }

    public void updateList() {
        mRecyclerAlunos.setAdapter(adapter);
        setupRecyclerView();
    }

    private void checkAdapterIsEmpty() {

        if (adapter.getItemCount() == 0) {
            emptyListImg.setVisibility(View.VISIBLE);
            emptyListText.setVisibility(View.VISIBLE);
            emptyListText.setText(R.string.txt_no_student);
        } else {
            emptyListImg.setVisibility(View.GONE);
            emptyListText.setVisibility(View.GONE);
        }

    }

    protected void setupRecyclerView() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });


        checkAdapterIsEmpty();
    }

}
