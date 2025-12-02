package com.microservices.microservicios.service;  
  
import com.microservices.microservicios.dto.CartItemDTO;  
import com.microservices.microservicios.dto.requests.CartItemRequest;  
import com.microservices.microservicios.model.CartItem;  
import com.microservices.microservicios.model.Product;  
import com.microservices.microservicios.model.User;  
import com.microservices.microservicios.repository.CartItemRepository;  
import com.microservices.microservicios.repository.ProductRepository;  
import com.microservices.microservicios.repository.UserRepository;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  
import org.springframework.transaction.annotation.Transactional;  
  
import java.math.BigDecimal;  
import java.util.List;  
import java.util.stream.Collectors;  
  
@Service  
@Transactional  
public class CartService {  
      
    @Autowired  
    private CartItemRepository cartItemRepository;  
      
    @Autowired  
    private UserRepository userRepository;  
      
    @Autowired  
    private ProductRepository productRepository;  
      
    public List<CartItemDTO> getCartByUser(String email) {  
        User user = userRepository.findByEmail(email)  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        List<CartItem> cartItems = cartItemRepository.findByUserOrderByCreatedAtDesc(user);  
        return cartItems.stream()  
            .map(this::convertToDTO)  
            .collect(Collectors.toList());  
    }  
      
    public CartItemDTO addToCart(String email, CartItemRequest request) {  
        User user = userRepository.findByEmail(email)  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        Product product = productRepository.findById(request.getProductCodigo())  
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));  
          
        // Verificar si el producto ya está en el carrito  
        CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product).orElse(null);  
          
        if (existingItem != null) {  
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());  
            CartItem updatedItem = cartItemRepository.save(existingItem);  
            return convertToDTO(updatedItem);  
        } else {  
            CartItem newItem = new CartItem();  
            newItem.setUser(user);  
            newItem.setProduct(product);  
            newItem.setQuantity(request.getQuantity());  
              
            CartItem savedItem = cartItemRepository.save(newItem);  
            return convertToDTO(savedItem);  
        }  
    }  
      
    public CartItemDTO updateQuantity(String email, Long itemId, Integer quantity) {  
        User user = userRepository.findByEmail(email)  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        CartItem cartItem = cartItemRepository.findByIdAndUser(itemId, user)  
            .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));  
          
        if (quantity <= 0) {  
            throw new RuntimeException("La cantidad debe ser mayor a 0");  
        }  
          
        cartItem.setQuantity(quantity);  
        CartItem updatedItem = cartItemRepository.save(cartItem);  
        return convertToDTO(updatedItem);  
    }  
      
    public void removeFromCart(String email, Long itemId) {  
        User user = userRepository.findByEmail(email)  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        CartItem cartItem = cartItemRepository.findByIdAndUser(itemId, user)  
            .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));  
          
        cartItemRepository.delete(cartItem);  
    }  
      
    public void clearCart(String email) {  
        User user = userRepository.findByEmail(email)  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        cartItemRepository.deleteByUser(user);  
    }  
      
    private CartItemDTO convertToDTO(CartItem cartItem) {  
        BigDecimal subtotal = cartItem.getProduct().getPrecio()  
            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));  
          
                return new CartItemDTO(
                    cartItem.getId(),
                    new com.microservices.microservicios.dto.ProductDTO(cartItem.getProduct()),
                    cartItem.getQuantity(),
                    cartItem.getCreatedAt(),
                    subtotal
                );    }  
}