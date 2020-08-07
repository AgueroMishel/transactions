package org.app;

import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import org.app.models.Statistics;
import org.app.models.Transaction;

@RestController
class TransactionsController {
    @Autowired
    private TransactionsHandler transactionsHandler;

    @GetMapping("transactions")
    public List<Transaction> getTransactions() {
        return transactionsHandler.getAllTransactions();
    }

    @PostMapping("/transactions")
    public ResponseEntity addTransaction(
            @RequestBody Transaction transaction) {
        return transactionsHandler.addSingleTransaction(transaction);
    }

    @ExceptionHandler({ValueInstantiationException.class, JsonParseException.class})
    public final ResponseEntity handleParseExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MismatchedInputException.class)
    public final ResponseEntity handleMismatchedInputException(Exception ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/transactions")
    public ResponseEntity deleteTransactions() {
        return transactionsHandler.deleteAllTransactions();
    }

    @GetMapping("statistics")
    public Statistics getStatics() {
        return transactionsHandler.getStatistics();
    }
}
