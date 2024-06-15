import com.sun.jdi.InvalidTypeException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>CFR</b> : Configuration For Rascals
 * <hr/>
 * The <b>CFR_Handler</b> class manages a custom configuration file format (CFR).
 * A <b>CFR</b> file is a file with the extension <b>.cfr</b><br/><br/><b>CFR</b> is <b>a tree-style format that contains holders and properties</b>.
 * Each holder can contain multiple unique properties, represented as key-value pairs.
 * <h4>Format:</h4>
 * <pre>
 * Holder {
 *     Key : Value
 *     Key : Value
 * }
 * Holder {
 *     Key : Value
 *     Key : Value
 * }
 * </pre>
 * <b>>Property</b> : The Key and Value(can't have duplicate in a single holder).<br/>
 *
 * <b>>Holder</b>   : A container for unique properties.
 * <hr><h4>Example:</h4>
 * <pre>
 * UserSettings {
 *     Theme : dark
 *     Language : en
 * }
 * DatabaseSettings {
 *     Host : localhost
 *     Port : 5432
 * }
 * </pre>
 */
public class CFR_Handler {

    /*
        TODO :
            [] = Comment validation
            [/] = Missing functions
    */

    private final File cfr;
    public static void main(String[] args) {}

    /**
     * Constructor for the CFR_Handler class
     * @param cfr any <b>.cfr</b> file
     */
    public CFR_Handler(File cfr) throws InvalidTypeException {
        if(cfr.getName().contains(".cfr")) {
            this.cfr = cfr;
        }else {
            throw new InvalidTypeException("Only .cfr files are valid...");
        }
    }

    // ======================================== POST / ADD ======================================== //

    /**
     * Creates a new holder, holder will only be created if
     * given holder name doesn't exist in the provided CFR file
     */
    public void createHolder(String name) throws IOException {
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");

        if(holderExists(name))
            return;

        FileWriter writer = new FileWriter(cfr, true);
        writer.write(name + " {");
        writer.write("\n}\n");
        writer.close();
    }

    /**
     * Creates a new Property corresponding to the given key name and assign an initial value to that new property
     */
    public void createProperty(String holder, String key, String value) throws IOException {
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");

        if(!holderExists(holder))
            return;
        if(propertyExists(holder, key))
            return;

        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        StringBuilder fileContent = new StringBuilder();

        String str;
        int index = -1;
        while((str = reader.readLine()) != null) {
            fileContent.append(str).append("\n");
            if(str.equals(holder + " {")) {
                index = fileContent.toString().length()-1;
            }
        }
        reader.close();
        if(index != -1) {
            fileContent.insert(index, "\n\t" + key + " : " + value);
        }

        FileWriter writer = new FileWriter(cfr);
        writer.write(fileContent.toString());
        writer.close();
    }


    // ======================================== GET / READ ======================================== //
    public boolean holderExists(String name) throws IOException {
        for(String h : getHolders()) {
            if(name.equalsIgnoreCase(h)) {
                return true;
            }
        }
        return false;
    }

    public boolean propertyExists(String holder, String key) throws IOException {
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");
        if(!holderExists(holder))
            return false;

        HashMap<String, String> properties = readHolder(holder);
        for(String k : properties.keySet()) {
            if(k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return a Hashmap that includes all the properties
     *             of the given holder name
     */
    public HashMap<String, String> readHolder(String holder) throws IOException {
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");

        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        ArrayList<String> lines = new ArrayList<>();

        String str;
        boolean flag = false;
        while((str = reader.readLine()) != null) {

            if(str.equals(holder + " {")) flag = true;
            if(flag) {
                if (str.contains("{")) {
                    continue;
                } else if (str.contains("}")) {
                    break;
                }
                lines.add(str.trim());
            }
        }

        HashMap<String, String> result = new HashMap<>();

        for(String s : lines) {
            if(s.contains(" : ")) {
                String[] split = s.split(" : ");
                result.put(split[0], split[1]);
            }
        }

        reader.close();
        return result;
    }

    /**
     * @return an array of String.
     *             this array will include all the holders found
     *             in the given CFR file
     */
    public String[] getHolders() throws IOException {

        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");

        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        StringBuilder builder = new StringBuilder();

        String str;
        while((str = reader.readLine()) != null) {
            if (str.contains("{"))
                builder.append(str).append("█");
        }
        reader.close();
        return builder.toString().trim().replaceAll(" \\{", "").split("█");
    }

    /**
     * @return a Property in the form of a String[] with a length of 2 :
     * <ul>
     *     <li>[0] = Key</li>
     *     <li>[1] = Value</li>
     * </ul>
     */
    public String[] getProperty(String holder, String key) throws IOException {
        // Returns the key and value by the given holder and key name
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");
        if(!holderExists(holder))
            throw new NullPointerException(holder + " : Holder doesn't exists");
        if(!propertyExists(holder, key))
            throw new NullPointerException(key + " : Property doesn't exists");
        String[] property = new String[2];

        HashMap<String, String> map = readHolder(holder);
        property[0] = key;
        property[1] = map.get(key);
        return property;
    }

    /**
     * @return the value of the given property(key) inside a given holder
     */
    public String getPropertyValue(String holder, String key) throws IOException {
        String value = "";
        value = getProperty(holder, key)[1];
        return value;
    }

    // ======================================== UPDATE ======================================== //

    /**
     * Updates the given property value into the passed parameter for a new value
     * <br><hr>
     * <small>Keys are non-editable, only the key value will be editable
     * Delete a key and create a new key if that's the scenario.</small>
     */
    public void updateProperty(String holder, String key, String newValue) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        StringBuilder fileContent = new StringBuilder();

        String str;
        int index = -1;
        while((str = reader.readLine()) != null) {
            fileContent.append(str).append("\n");
            if(str.equals(holder + " {")) {
                index = fileContent.toString().length()-1;
            }
        }
        reader.close();

        int holderStart = index;
        int holderEnd = fileContent.indexOf("}", holderStart);

        String holderSection = fileContent.substring(holderStart, holderEnd + 1);
        String oldValue = getPropertyValue(holder, key);
        String keyValuePattern = key + " : " + oldValue;
        int keyIndex = holderSection.indexOf(keyValuePattern);
        if(keyIndex != -1) {
            int keyValueLength = keyValuePattern.length();
            String newKeyValue = key + " : " + newValue;
            holderSection = holderSection.substring(0, keyIndex) + newKeyValue + holderSection.substring(keyIndex + keyValueLength);
        }

        fileContent.replace(holderStart, holderEnd + 1, holderSection);

        FileWriter writer = new FileWriter(cfr);
        writer.write(fileContent.toString());
        writer.close();
    }
    // ======================================== DELETE ======================================== //

    /**
     * Deletes the appropriate Holder based on the given parameter
     * @return -- {@code true} -- if the operation is success otherwise, -- {@code false} --
     */
    public boolean deleteHolder(String holder) throws IOException {
        if(!cfr.exists())
            throw new FileNotFoundException("CFR file not found.");
        if(!holderExists(holder))
            throw new NullPointerException(holder + " : Holder doesn't exists");

        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        StringBuilder fileContent = new StringBuilder();

        String str;
        int index = -1;
        String holderString = "";
        while((str = reader.readLine()) != null) {
            fileContent.append(str).append("\n");
            if(str.equals(holder + " {")) {
                holderString = str;
                index = fileContent.toString().length()-1;
            }
        }
        reader.close();

        int holderStart = index;
        int holderEnd = fileContent.indexOf("}", holderStart);

        fileContent.replace(holderStart - holderString.length(), holderEnd + 1, "");
        FileWriter writer = new FileWriter(cfr);
        writer.write(fileContent.toString());
        writer.close();
        return true;
    }

    /**
     * Deletes the appropriate property from a given holder
     * @return -- {@code true} -- if the operation is success otherwise, -- {@code false} --
     */
    public boolean deleteProperty(String holder, String key) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(cfr));
        StringBuilder fileContent = new StringBuilder();

        String str;
        int index = -1;
        while((str = reader.readLine()) != null) {
            fileContent.append(str).append("\n");
            if(str.equals(holder + " {")) {
                index = fileContent.toString().length()-1;
            }
        }
        reader.close();

        int holderStart = index;
        int holderEnd = fileContent.indexOf("}", holderStart);

        String holderSection = fileContent.substring(holderStart, holderEnd + 1);
        String oldValue = getPropertyValue(holder, key);
        String keyValuePattern = key + " : " + oldValue;
        int keyIndex = holderSection.indexOf(keyValuePattern);
        if(keyIndex != -1) {
            int keyValueLength = keyValuePattern.length();
            holderSection = holderSection.substring(0, keyIndex).trim() + holderSection.substring(keyIndex + keyValueLength);
        }
        fileContent.replace(holderStart, holderEnd + 1, holderSection);

        FileWriter writer = new FileWriter(cfr);
        writer.write(fileContent.toString());
        writer.close();
        return true;
    }
}