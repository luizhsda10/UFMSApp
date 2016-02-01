package ufms.br.com.ufmsapp.pojo;


public class AlunoXDisciplina {

    int id;
    int aluno;
    int disciplina;
    int statusDisciplina;
    int turma;
    int alunoXDisciplinaIdServidor;

    public AlunoXDisciplina(int id, int aluno, int disciplina, int statusDisciplina, int turma, int alunoXDisciplinaIdServidor) {
        this.id = id;
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.statusDisciplina = statusDisciplina;
        this.turma = turma;
        this.alunoXDisciplinaIdServidor = alunoXDisciplinaIdServidor;
    }

    public AlunoXDisciplina() {
    }

    public int getAlunoXDisciplinaIdServidor() {
        return alunoXDisciplinaIdServidor;
    }

    public void setAlunoXDisciplinaIdServidor(int alunoXDisciplinaIdServidor) {
        this.alunoXDisciplinaIdServidor = alunoXDisciplinaIdServidor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAluno() {
        return aluno;
    }

    public void setAluno(int aluno) {
        this.aluno = aluno;
    }

    public int getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(int disciplina) {
        this.disciplina = disciplina;
    }

    public int getStatusDisciplina() {
        return statusDisciplina;
    }

    public void setStatusDisciplina(int statusDisciplina) {
        this.statusDisciplina = statusDisciplina;
    }

    public int getTurma() {
        return turma;
    }

    public void setTurma(int turma) {
        this.turma = turma;
    }

    @Override
    public String toString() {
        return "AlunoXDisciplina{" +
                "id=" + id +
                ", aluno=" + aluno +
                ", disciplina=" + disciplina +
                ", statusDisciplina=" + statusDisciplina +
                ", turma=" + turma +
                '}';
    }
}
