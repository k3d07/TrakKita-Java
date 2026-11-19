package trakkita.model;

/** Anything that can be written as one line in the data file. */
public interface Persistable {
    String toCsvLine();
}
