package com.microservices.microservicios.service;

import com.microservices.microservicios.dto.CategoryDTO;
import com.microservices.microservicios.dto.SubCategoryDTO;
import com.microservices.microservicios.dto.requests.CategoryRequest;
import com.microservices.microservicios.model.Category;
import com.microservices.microservicios.model.Subcategory;
import com.microservices.microservicios.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
        return convertToDTO(category);
    }

    public CategoryDTO getCategoryByTitle(String title) {
        Category category = categoryRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + title));
        return convertToDTO(category);
    }

    public CategoryDTO createCategory(CategoryRequest request) {
        if (categoryRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("El título de la categoría ya existe");
        }

        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setLink(request.getLink());

        for (var subcatRequest : request.getSubcategories()) {
            Subcategory subcategory = new Subcategory();
            subcategory.setName(subcatRequest.getName());
            subcategory.setLink(subcatRequest.getLink());
            subcategory.setCategory(category);
            category.getSubcategories().add(subcategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    // --- AQUÍ ESTÁ LA CORRECCIÓN CLAVE ---
    public CategoryDTO updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));

        // 1. Actualizar datos de la categoría padre
        category.setTitle(request.getTitle());
        category.setLink(request.getLink());

        // 2. Obtener los IDs de las subcategorías que vienen en el Request
        // (Asegúrate de que tu SubcategoryRequest tenga el campo 'id')
        List<Long> incomingIds = request.getSubcategories().stream()
                .map(sub -> sub.getId()) 
                .filter(subId -> subId != null)
                .collect(Collectors.toList());

        // 3. Borrar SOLO las que ya no están en la lista (Orphan Removal hará el delete físico)
        category.getSubcategories().removeIf(existingSub -> 
                existingSub.getId() != null && !incomingIds.contains(existingSub.getId())
        );

        // 4. Recorrer la lista para Actualizar existentes o Crear nuevas
        for (var subReq : request.getSubcategories()) {
            
            if (subReq.getId() != null) {
                // CASO A: Ya tiene ID -> Buscamos y Actualizamos
                Subcategory existingSub = category.getSubcategories().stream()
                        .filter(s -> s.getId().equals(subReq.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingSub != null) {
                    existingSub.setName(subReq.getName());
                    existingSub.setLink(subReq.getLink());
                    // No tocamos el ID, se mantiene el mismo
                }
            } else {
                // CASO B: No tiene ID -> Es nueva -> Creamos
                Subcategory newSub = new Subcategory();
                newSub.setName(subReq.getName());
                newSub.setLink(subReq.getLink());
                newSub.setCategory(category);
                
                category.getSubcategories().add(newSub);
            }
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        List<SubCategoryDTO> subcategoryDTOs = category.getSubcategories().stream()
                .map(sub -> new SubCategoryDTO(sub.getId(), sub.getName(), sub.getLink()))
                .collect(Collectors.toList());
        return new CategoryDTO(
                category.getId(),   
                category.getTitle(),
                category.getLink(),
                subcategoryDTOs
        );
    }
}