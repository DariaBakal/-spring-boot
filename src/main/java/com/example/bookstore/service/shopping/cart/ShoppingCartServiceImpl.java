package com.example.bookstore.service.shopping.cart;

import com.example.bookstore.dto.book.AddBookToCartRequestDto;
import com.example.bookstore.dto.shoping.cart.UpdateQuantityRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.shopping.cart.CartItemRepository;
import com.example.bookstore.repository.shopping.cart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public ShoppingCart addBookToCart(User user, AddBookToCartRequestDto requestDto) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUser(user);

        Optional<CartItem> existingCartItem = userShoppingCart.getCartItems().stream()
                .filter(b -> b.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int currentQuantity = cartItem.getQuantity();
            cartItem.setQuantity(currentQuantity + requestDto.getQuantity());
            return shoppingCartRepository.save(userShoppingCart);
        }

        Book bookFromDb = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "No book found with id: " + requestDto.getBookId()));

        CartItem cartItem = new CartItem();
        cartItem.setBook(bookFromDb);
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(userShoppingCart);
        userShoppingCart.getCartItems().add(cartItem);
        return shoppingCartRepository.save(userShoppingCart);
    }

    @Override
    public ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.findByUser(user);
    }

    @Override
    public ShoppingCart updateBookQuantity(User user, Long cartItemId,
            UpdateQuantityRequestDto requestDto) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUser(user);
        Optional<CartItem> cartItemFromCart = userShoppingCart.getCartItems().stream()
                .filter(b -> b.getId().equals(cartItemId))
                .findFirst();
        if (cartItemFromCart.isPresent()) {
            CartItem cartItem = cartItemFromCart.get();
            cartItem.setQuantity(requestDto.getQuantity());
            return shoppingCartRepository.save(userShoppingCart);
        } else {
            throw new EntityNotFoundException(
                    "Can't find item with id %s in the shopping cart".formatted(cartItemId));
        }
    }

    @Override
    public void removeBook(User user, Long cartItemId) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUser(user);
        Optional<CartItem> cartItemFromCart = userShoppingCart.getCartItems().stream()
                .filter(b -> b.getId().equals(cartItemId))
                .findFirst();
        if (cartItemFromCart.isPresent()) {
            CartItem cartItem = cartItemFromCart.get();
            userShoppingCart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            throw new EntityNotFoundException(
                    "Can't find item with id %s in the shopping cart".formatted(cartItemId));
        }
    }
}
