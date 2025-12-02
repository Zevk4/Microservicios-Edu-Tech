package com.microservices.microservicios.controller;

import com.microservices.microservicios.config.JwtTokenProvider; // Import JwtTokenProvider
import com.microservices.microservicios.dto.CartItemDTO;
import com.microservices.microservicios.dto.requests.CartItemRequest;
import com.microservices.microservicios.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; 

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(cartService.getCartByUser(email));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addToCart(@Valid @RequestBody CartItemRequest request,
                                                 @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(cartService.addToCart(email, request));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemDTO> updateQuantity(@PathVariable Long itemId,
                                                     @Valid @RequestBody CartItemRequest request,
                                                     @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(cartService.updateQuantity(email, itemId, request.getQuantity()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId,
                                               @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        cartService.removeFromCart(email, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }

    // Helper method to extract email from token
    private String getEmailFromToken(String token) {
        String jwt = token.substring(7); 
        return jwtTokenProvider.getEmailFromToken(jwt);
    }
}
