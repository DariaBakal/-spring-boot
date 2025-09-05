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
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCart addBookToCart(User user, AddBookToCartRequestDto requestDto) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUserId(user.getId());
        Book bookFromDb = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "No book found with id: " + requestDto.getBookId()));

        Optional<CartItem> existingCartItem = userShoppingCart.getCartItems().stream()
                .filter(b -> b.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int currentQuantity = cartItem.getQuantity();
            cartItem.setQuantity(currentQuantity + requestDto.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setBook(bookFromDb);
            cartItem.setQuantity(requestDto.getQuantity());
            cartItem.setShoppingCart(userShoppingCart);
            userShoppingCart.getCartItems().add(cartItem);
        }
        cartItemRepository.save(cartItem);
        return userShoppingCart;
    }

    @Override
    public ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.findByUserId(user.getId());
    }

    @Override
    public ShoppingCart updateBookQuantity(User user, Long cartItemId,
            UpdateQuantityRequestDto requestDto) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUserId(user.getId());
        Long shoppingCartId = userShoppingCart.getId();
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCartId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find item with id %s in the shopping cart".formatted(cartItemId)));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return userShoppingCart;
    }

    @Override
    public void removeBook(User user, Long cartItemId) {
        ShoppingCart userShoppingCart = shoppingCartRepository.findByUserId(user.getId());
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                        userShoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find item with id %s in the shopping cart".formatted(cartItemId)));
        userShoppingCart.getCartItems().remove(cartItem);
        shoppingCartRepository.save(userShoppingCart);
    }

    @Override
    public ShoppingCart createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }
}
