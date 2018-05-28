package ir.systemco.ssaj.yolrelease2.model;

public class VahedSequence {
    private String Name;
    private int Num;

    public VahedSequence(String name, int num) {

        this.Name = name;
        this.Num = num;
    }
    public String getName() {

        return Name;
    }
    public int getNum() {

        return Num;
    }
}
