package ru.clevertec.lucene;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.exception.CommentIndexException;
import ru.clevertec.util.Constants;

@Transactional
@Component
public class Indexer {

    private final EntityManager entityManager;

    private static final int THREAD_NUMBER = 4;

    public Indexer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void indexPersistedData(String indexClassName) throws CommentIndexException {

        try {
            SearchSession searchSession = Search.session(entityManager);

            Class<?> classToIndex = Class.forName(indexClassName);
            MassIndexer indexer =
                    searchSession
                            .massIndexer(classToIndex)
                            .threadsToLoadObjects(THREAD_NUMBER);

            indexer.startAndWait();
        } catch (ClassNotFoundException e) {
            throw CommentIndexException.getInstance(
                    String.format(Constants.ERROR_INVALID_CLASS, indexClassName));
        } catch (InterruptedException e) {
            throw CommentIndexException.getInstance(Constants.ERROR_INDEX_INTERRUPTED);
        }
    }
}