package com.microservices.microservicios.service;  
  
import com.microservices.microservicios.dto.ProductDTO;  
import com.microservices.microservicios.dto.requests.ProductRequest;  
import com.microservices.microservicios.model.Product;  
import com.microservices.microservicios.repository.ProductRepository;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  
import org.springframework.transaction.annotation.Transactional;  
  
import java.math.BigDecimal;  
import java.util.List;  
import java.util.stream.Collectors;  
  
@Service  
@Transactional  
public class ProductService {  
      
    @Autowired  
    private ProductRepository productRepository;  
      
    public List<ProductDTO> getAllProducts() {  
        return productRepository.findAll().stream()  
            .map(ProductDTO::new)  
            .collect(Collectors.toList());  
    }  
      
    public ProductDTO getProductByCodigo(String codigo) {  
        Product product = productRepository.findById(codigo)  
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));  
        return new ProductDTO(product);  
    }  
      
    public ProductDTO createProduct(ProductRequest request) {  
        Product product = new Product();  
        product.setCodigo(generateCodigo(request.getSubcategoria()));  
        product.setNombre(request.getNombre());  
        product.setDescripcion(request.getDescripcion());  
        product.setPrecio(request.getPrecio());  
        product.setCategoria(request.getCategoria());  
        product.setSubcategoria(request.getSubcategoria());  
        product.setImagen(request.getImagen());  
        product.setMarca(request.getMarca());  
          
        Product savedProduct = productRepository.save(product);  
        return new ProductDTO(savedProduct);  
    }  
      
    public ProductDTO updateProduct(String codigo, ProductRequest request) {  
        Product product = productRepository.findById(codigo)  
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));  
              
        product.setNombre(request.getNombre());  
        product.setDescripcion(request.getDescripcion());  
        product.setPrecio(request.getPrecio());  
        product.setCategoria(request.getCategoria());  
        product.setSubcategoria(request.getSubcategoria());  
        product.setImagen(request.getImagen());  
        product.setMarca(request.getMarca());  
          
        Product savedProduct = productRepository.save(product);  
        return new ProductDTO(savedProduct);  
    }  
      
    public void deleteProduct(String codigo) {  
        if (!productRepository.existsById(codigo)) {  
            throw new RuntimeException("Producto no encontrado");  
        }  
        productRepository.deleteById(codigo);  
    }  
      
    public List<ProductDTO> searchProducts(String query) {  
        List<Product> products = productRepository.findByNombreContainingIgnoreCase(query);  
        return products.stream()  
            .map(ProductDTO::new)  
            .collect(Collectors.toList());  
    }  
      
    public List<ProductDTO> filterProducts(String marca, String categoria, String precioRange) {  
        List<Product> products = productRepository.findAll();  
          
        if (marca != null && !marca.isEmpty()) {  
            products = products.stream()  
                .filter(p -> p.getMarca().equals(marca))  
                .collect(Collectors.toList());  
        }  
          
        if (categoria != null && !categoria.isEmpty()) {  
            products = products.stream()  
                .filter(p -> p.getCategoria().equals(categoria))  
                .collect(Collectors.toList());  
        }  
          
        if (precioRange != null && !precioRange.isEmpty()) {  
            BigDecimal min = BigDecimal.ZERO;  
            BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);  
              
            switch (precioRange) {  
                case "0-50000":  
                    max = BigDecimal.valueOf(50000);  
                    break;  
                case "50001-200000":  
                    min = BigDecimal.valueOf(50001);  
                    max = BigDecimal.valueOf(200000);  
                    break;  
                case "200001-max":  
                    min = BigDecimal.valueOf(200001);  
                    break;  
            }  
              
            final BigDecimal finalMin = min;  
            final BigDecimal finalMax = max;  
            products = products.stream()  
                .filter(p -> p.getPrecio().compareTo(finalMin) >= 0 && p.getPrecio().compareTo(finalMax) <= 0)  
                .collect(Collectors.toList());  
        }  
          
        return products.stream()  
            .map(ProductDTO::new)  
            .collect(Collectors.toList());  
    }  
      
    private String generateCodigo(String subcategoria) {  
        String prefix = subcategoria.substring(0, 3).toUpperCase();  
        long count = productRepository.countBySubcategoriaStartingWith(subcategoria);  
        return prefix + String.format("%03d", count + 1);  
    }  
}