package app;

import app.book.entities.Item;
import org.hibernate.Session;

/**
 * Created by ajoshi on 30-Sep-16.
 */
public interface TransactionWorker {
    void doInTransaction(Item item);
}
