
public class SymbolTableValues {

    String type, kind;
    int index;

    public SymbolTableValues(String type, String kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType(){
        return type;
    }

    public String getKind(){
        return kind;
    }

    public int getIndex(){
        return index;
    }
}
