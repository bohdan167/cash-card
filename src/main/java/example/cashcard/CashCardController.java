package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @PostAuthorize("returnObject.body.owner == authentication.name")
    @GetMapping("/{requestId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestId) {
        return this.cashCardRepository.findById(requestId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(@RequestBody CashCardRequest cashCardRequest, UriComponentsBuilder ucb, @CurrentOwner String owner) {
        CashCard cashCard = new CashCard(cashCardRequest.amount(), owner);
        CashCard savedCashCard = this.cashCardRepository.save(cashCard);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).body(savedCashCard);
    }

    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll() {
        return ResponseEntity.ok(this.cashCardRepository.findAll());
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<CashCard> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate) {
        CashCard cashCard = this.cashCardRepository.findById(requestedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        CashCard updatedCashCard = new CashCard(requestedId, cashCardUpdate.amount(), cashCard.owner());
        CashCard savedCashCard = this.cashCardRepository.save(updatedCashCard);
        return ResponseEntity.ok(savedCashCard);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id) {
        this.cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
