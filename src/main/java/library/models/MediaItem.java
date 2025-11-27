package library.models;



/**
 * Interface for all media items in the library (Books, CDs, etc.)
 * @author Library Team
 * @version 1.0
 */
public interface MediaItem {
    String getId();
    String getTitle();
    boolean isAvailable();
    void setAvailable(boolean available);
    String getType();
}