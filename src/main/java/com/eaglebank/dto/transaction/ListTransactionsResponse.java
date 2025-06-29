package com.eaglebank.dto.transaction;

import lombok.Data;

import java.util.List;

@Data
public class ListTransactionsResponse {

    List<TransactionResponse> transactions;

}
