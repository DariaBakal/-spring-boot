package com.example.bookstore.controller;

import com.example.bookstore.dto.book.AddBookToCartRequestDto;
import com.example.bookstore.dto.shoping.cart.ShoppingCartDto;
import com.example.bookstore.dto.shoping.cart.UpdateQuantityRequestDto;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.User;
import com.example.bookstore.service.shopping.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add book to the shopping cart",
            description = "Add a new book to the user's shopping cart. If the book is already in "
                    + "the cart, the quantity will be increased")
    public ShoppingCartDto addBookToCart(@RequestBody @Valid AddBookToCartRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return shoppingCartMapper.toDto(shoppingCartService.addBookToCart(user, requestDto));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get shopping cart",
            description = "Retrieves the user's shopping cart to view its contents before placing "
                    + "an order")
    public ShoppingCartDto getShoppingCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return shoppingCartMapper.toDto(shoppingCartService.getShoppingCart(user));
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update book quantity",
            description = "Updates the quantity of a specific book in the shopping cart")
    public ShoppingCartDto updateBookQuantity(@PathVariable Long cartItemId,
            @RequestBody @Valid UpdateQuantityRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return shoppingCartMapper.toDto(
                shoppingCartService.updateBookQuantity(user, cartItemId, requestDto));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remove books",
            description = "Removes a book from the shopping cart")
    public void removeBook(@PathVariable Long cartItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        shoppingCartService.removeBook(user, cartItemId);
    }
}
