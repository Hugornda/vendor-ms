package com.github.Hugornda.vendor_ms.config.execption;

import com.github.Hugornda.vendor_ms.model.exceptions.VendorAlreadyExistsException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.concurrent.CompletableFuture;


@ControllerAdvice
public class VendorExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        GraphQLError graphQLError;
        Throwable exception = handlerParameters.getException();
        if (exception instanceof VendorAlreadyExistsException) {
            graphQLError = getDuplicatedVendorGraphQLError();
        }else {
            graphQLError = getDefaultGraphqlError(exception);
        }
        DataFetcherExceptionHandlerResult exceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                .error(graphQLError)
                .build();
         return CompletableFuture.completedFuture(exceptionHandlerResult);
    }

    private static GraphQLError getDefaultGraphqlError(Throwable exception) {
        return GraphqlErrorBuilder.newError()
                .message(exception.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }

    private static GraphQLError getDuplicatedVendorGraphQLError() {
        return GraphqlErrorBuilder.newError()
                .message("A vendor with this name already exists.")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }
}
