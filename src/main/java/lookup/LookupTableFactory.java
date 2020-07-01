package lookup;

public class LookupTableFactory {
    // TODO Move these perhaps to a settings file of some sort
    public final String DEFAULT_FACTORY_TYPE = "Concurrent";
    public final int DEFAULT_TABLE_SIZE = 30;

    public LookupTable createDefaultLookupTable(){
        return createLookupTable(DEFAULT_FACTORY_TYPE, DEFAULT_TABLE_SIZE);
    }

    public LookupTable createLookupTable(String type, int tableSize){
        // TODO log an error when logging is set up
        switch (type) {
            case "Concurrent": return new ConcurrentLookupTable(tableSize);
            default: return null;
        }
    }
}
