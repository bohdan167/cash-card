package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface CashCardRepository extends
        CrudRepository<CashCard, Long>,
        PagingAndSortingRepository<CashCard, Long>
{
    CashCard findByIdAndOwner(Long id, String owner);

    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);

    boolean existsByIdAndOwner(Long id, String owner);

    @Query("select * from cash_card cc where cc.owner = :#{authentication.name}")
	Page<CashCard> findAll(PageRequest pageRequest);
}
