package com.example.bookstore.service.shopping.cart;

import com.example.bookstore.dto.book.AddBookToCartRequestDto;
import com.example.bookstore.dto.shoping.cart.UpdateQuantityRequestDto;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCart addBookToCart(User user, AddBookToCartRequestDto requestDto);

    ShoppingCart getShoppingCart(User user);

    ShoppingCart updateBookQuantity(User user, Long cartItemId,
            UpdateQuantityRequestDto requestDto);

    void removeBook(User user, Long cartItemId);
}
