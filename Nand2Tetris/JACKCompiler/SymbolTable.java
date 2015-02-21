
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class SymbolTable {
    static final String Static = "STATIC";
    static final String Field = "FIELD";
    static final String Arg = "ARG";
    static final String Var = "VAR";

    int argIndex, varIndex, staticIndex, fieldIndex;

    Hashtable<String, SymbolTableValues> classScope;
    Hashtable<String, SymbolTableValues> subScope;
    Hashtable<String, SymbolTableValues> currentScope;

    //Creates a new empty symbol table
    public SymbolTable() {
        classScope = new Hashtable<String, SymbolTableValues>();
        subScope = new Hashtable<String, SymbolTableValues>();
        currentScope = classScope;
        argIndex = 0;
        varIndex = 0;
        staticIndex = 0;
        fieldIndex = 0;
    }

    //Starts a new subroutine scope
    public void startSubroutine(String subtype) {
        //Start by cleaning the subScope
        subScope.clear();

        //Jump into the subScope by setting it to be current
        currentScope = subScope;

        //If the subtype is a method
        argIndex = subtype.equals("method") ? 1 : 0;
        varIndex = 0;
    }

    //Defines a new identifier of name, type, and kind and assigns it an index
    public void define(String name, String type, String kind) {
        int index = -1;
        SymbolTableValues stv = null;

        if(kind.equals(Static) || kind.equals(Field)) {

            if (kind.equals(Static)){
                index = staticIndex++;
            } else {
                index = fieldIndex++;
            }

            stv = classScope.put(name, new SymbolTableValues(type, kind, index));
        } else {
            if (kind.equals(Arg) || kind.equals(Var)) {

                if (kind.equals(Arg)) {
                    index = argIndex++;
                } else {
                    index = varIndex++;
                }

                stv = subScope.put(name, new SymbolTableValues(type, kind, index));
            }
        }
    }

    //Returns the number of variables of the given kind already defined in the current scope
    public int varCount(String kind) {
        int count = 0;
        Hashtable<String, SymbolTableValues> scope = null;

        //Also can use a StringTokenizer here but Enumeration is a little better suited
        Enumeration<String> enumeration;

        if(kind.equals(SymbolTable.Var) || kind.equals(SymbolTable.Arg)) {
            scope = subScope;
        } else {

            if(kind.equals(SymbolTable.Field) || kind.equals(SymbolTable.Static)) {
                scope = classScope;
            }
        }

        enumeration = scope.keys();

        while(enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();

            if(scope.get(key) != null && scope.get(key).getKind().equals(kind)) {
                count++;
            }
        }
        return count;
    }

    //Returns the kind of the named identifier in the current scope
    public String kindOf(String name) {
        SymbolTableValues stv = currentScope.get(name);

        if(stv != null)
            return stv.getKind();

        if(currentScope != classScope) {
            stv = classScope.get(name);

            if (stv != null) {
                return stv.getKind();
            }
        }
        return "NONE";
    }

    //Returns the type of the named identifier in the current scope
    public String typeOf(String name) {
        SymbolTableValues stv = currentScope.get(name);

        if(stv != null)
            return stv.getType();

        if(currentScope != classScope) {
            stv = classScope.get(name);

            if(stv != null) {
                return stv.getType();
            }
        }
        return null;
    }

    //Returns the index assigned to the named identifier
    public int indexOf(String name) {
        SymbolTableValues stv = currentScope.get(name);

        if(stv != null)
            return stv.getIndex();

        if(currentScope != classScope) {
            stv = classScope.get(name);

            if(stv != null) {
                return stv.getIndex();
            }
        }
        return -1;
    }
}