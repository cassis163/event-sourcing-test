package com.example.eventstoredbdemo.config;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import com.eventstore.dbclient.EventStoreDBProjectionManagementClient;
import com.eventstore.dbclient.ParseError;
import com.eventstore.dbclient.ReadResult;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.ResolvedEvent;
import com.example.eventstoredbdemo.event.AccountCreatedEvent;
import com.example.eventstoredbdemo.event.Event;
import com.example.eventstoredbdemo.event.FundsDepositedEvent;
import com.example.eventstoredbdemo.projection.AmountProjection;
import com.example.eventstoredbdemo.state.FundsState;

@Configuration()
public class EventStoreDbConfig {
    private final String CONNECTION_STRING = "esdb://admin:changeit@127.0.0.1:2113?tls=false&tlsVerifyCert=false";

    private final String ACCOUNT_STREAM_NAME = "account-stream";
    private final String TRANSACTION_STREAM_NAME = "transaction-stream";

    private final String PROJECTION_NAME = "funds-projection";

    private EventStoreDBClientSettings settings;
    private EventStoreDBClient client;
    private EventStoreDBProjectionManagementClient projectionManagementClient;

    private Random random = new Random();
    private Logger logger = LoggerFactory.getLogger(EventStoreDbConfig.class);

    @PostConstruct
    public void initialize() throws ParseError {
        this.settings = EventStoreDBConnectionString
                .parse(this.CONNECTION_STRING);
        this.client = EventStoreDBClient.create(settings);
        this.projectionManagementClient = EventStoreDBProjectionManagementClient.create(this.settings);
    }

    @Bean
    public void run() throws InterruptedException, ExecutionException, IOException {
        this.addTransactionsToStream();
        // Wait for the transactions to be added to the stream
        Thread.sleep(1000);
        this.createProjection();
        Thread.sleep(1000);
        this.logProjectionResult();
        Thread.sleep(5000);
    }

    private void addTransactionsToStream() {
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent(1);
        this.registerEvent(accountCreatedEvent, "accountCreatedEvent", this.ACCOUNT_STREAM_NAME);

        for (int i = 0; i < 10; i++) {
            FundsDepositedEvent event = new FundsDepositedEvent(1, this.random.nextFloat(0, 100));

            this.registerEvent(event, "FundsDepositedEvent", this.TRANSACTION_STREAM_NAME);
        }
    }

    private void logProjectionResult() throws InterruptedException {
        CompletableFuture<AmountProjection> result = this.projectionManagementClient.getResult(this.PROJECTION_NAME,
                AmountProjection.class);

        // result.thenAccept(projection -> {
        //     this.logger.info(String.format("%s events", projection.getCount()));
        // });

        Thread.sleep(4000);

        try {
            this.logger.info(String.format("Funds: %s", result.get().getAmount()));
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createProjection() {
        String query = """
                fromStream('transaction-stream')
                .when({
                    $init: function() {
                        return {
                            amount: 0
                        };
                    },
                    $any: function(s, e) {
                        s.amount += e.data.amount;
                    }
                })
                .outputState();
                    """;

        this.projectionManagementClient.create(this.PROJECTION_NAME, query);
    }

    private void registerEvent(Event event, String eventType, String streamName) {
        EventData eventData = EventData
                .builderAsJson(eventType, event)
                .build();

        this.client.appendToStream(streamName, eventData);
    }
}
