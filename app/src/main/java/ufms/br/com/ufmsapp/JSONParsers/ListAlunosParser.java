
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

package ufms.br.com.ufmsapp.JSONParsers;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ufms.br.com.ufmsapp.extras.Keys;
import ufms.br.com.ufmsapp.pojo.Aluno;
import ufms.br.com.ufmsapp.utils.Constants;
import ufms.br.com.ufmsapp.utils.JSONUtils;

public class ListAlunosParser {

    public static ArrayList<Aluno> parseAlunoJSON(JSONObject response) {
        ArrayList<Aluno> listAlunos = new ArrayList<>();

        if (response != null && response.length() > 0) {

            String nomeAluno = Constants.NA;
            String emailAluno = Constants.NA;
            String rgaAluno = Constants.NA;
            int statusAluno = -1;
            int idServidorAluno = -1;


            try {
                JSONArray jsonArray = response.getJSONArray(Keys.AlunosEndpointColumns.KEY_ALUNOS);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject currentObject = jsonArray.getJSONObject(i);

                    if (JSONUtils.contains(currentObject, Keys.AlunosEndpointColumns.KEY_NOME_ALUNO)) {
                        nomeAluno = currentObject.getString(Keys.AlunosEndpointColumns.KEY_NOME_ALUNO);
                    }

                    if (JSONUtils.contains(currentObject, Keys.AlunosEndpointColumns.KEY_EMAIL_ALUNO)) {
                        emailAluno = currentObject.getString(Keys.AlunosEndpointColumns.KEY_EMAIL_ALUNO);
                    }

                    if (JSONUtils.contains(currentObject, Keys.AlunosEndpointColumns.KEY_RGA_ALUNO)) {
                        rgaAluno = currentObject.getString(Keys.AlunosEndpointColumns.KEY_RGA_ALUNO);
                    }

                    if (JSONUtils.contains(currentObject, Keys.AlunosEndpointColumns.KEY_STATUS_ALUNO_FK)) {
                        statusAluno = currentObject.getInt(Keys.AlunosEndpointColumns.KEY_STATUS_ALUNO_FK);
                    }

                    if (JSONUtils.contains(currentObject, Keys.AlunosEndpointColumns.KEY_ID_ALUNOS)) {
                        idServidorAluno = currentObject.getInt(Keys.AlunosEndpointColumns.KEY_ID_ALUNOS);
                    }


                    Aluno aluno = new Aluno();

                    aluno.setNome(nomeAluno);
                    aluno.setEmail(emailAluno);
                    aluno.setRga(rgaAluno);

                    if (idServidorAluno != -1 && statusAluno != -1) {
                        aluno.setStatusAluno(statusAluno);
                        aluno.setAlunoIdServidor(idServidorAluno);
                    }

                    listAlunos.add(aluno);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return listAlunos;

    }
}
