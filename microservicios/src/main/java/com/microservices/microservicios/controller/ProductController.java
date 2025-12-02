package com.microservices.microservicios.controller;  
  
import com.microservices.microservicios.dto.ProductDTO;  
import com.microservices.microservicios.dto.requests.ProductRequest;  
import com.microservices.microservicios.service.ProductService;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.*;  
  
import jakarta.validation.Valid;  
import java.util.List;  
  
@RestController  
@RequestMapping("/api/products")  
@CrossOrigin(origins = "*")  
public class ProductController {  
      
    @Autowired  
    private ProductService productService;  
      
    @GetMapping  
    public ResponseEntity<List<ProductDTO>> getAllProducts() {  
        return ResponseEntity.ok(productService.getAllProducts());  
    }  
      
    @GetMapping("/{codigo}")  
    public ResponseEntity<ProductDTO> getProductByCodigo(@PathVariable String codigo) {  
        return ResponseEntity.ok(productService.getProductByCodigo(codigo));  
    }  
      
    @PostMapping("/create")  
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequest request) {  
        return ResponseEntity.ok(productService.createProduct(request));  
    }  
      
    @PutMapping("/{codigo}")  
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String codigo,   
                                                   @Valid @RequestBody ProductRequest request) {  
        return ResponseEntity.ok(productService.updateProduct(codigo, request));  
    }  
      
    @DeleteMapping("/{codigo}")  
    public ResponseEntity<Void> deleteProduct(@PathVariable String codigo) {  
        productService.deleteProduct(codigo);  
        return ResponseEntity.noContent().build();  
    }  
      
    @GetMapping("/filter")  
    public ResponseEntity<List<ProductDTO>> filterProducts(  
            @RequestParam(required = false) String marca,  
            @RequestParam(required = false) String categoria,  
            @RequestParam(required = false) String precioRange) {  
        return ResponseEntity.ok(productService.filterProducts(marca, categoria, precioRange));  
    }  
      
    @GetMapping("/search")  
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String q) {  
        return ResponseEntity.ok(productService.searchProducts(q));  
    }  
}