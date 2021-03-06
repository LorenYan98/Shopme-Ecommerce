package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	static void deleteExtraImageWereRemoved(Product product) {
		String extraImage = "../product-images/" + product.getId() + "/extras";
		Path dirpath = Paths.get(extraImage);
		
		try {
			Files.list(dirpath).forEach(file ->{
				String fileName = file.toFile().getName();
				
				if(!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Delete extra image: " + fileName);
					}catch (IOException e) {
						LOGGER.error("Could not delete extra image: " + fileName);
					}
				}
			});
		} catch (IOException e) {
			LOGGER.error("Could not list directory: " + dirpath);
		}
	}

	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		if(imageIDs == null || imageIDs.length == 0) {
			return;
		}
		Set<ProductImage> images = new HashSet<>();
		for(Integer count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			images.add(new ProductImage(id, name, product));
		}
		product.setImages(images);
		
		
	}

	static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, 
			Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			if(id != 0) {
				product.addExtraDetails(id, name, value);
			}else if(!name.isEmpty() && !value.isEmpty()) {
				product.addExtraDetails(name, value);
			}
		}
		
	}

	static void saveUploadedImages(MultipartFile mainMultipartFile, 
			MultipartFile[] extraMultipartFiles,
			Product savedProduct) throws IOException {
		
		// Save main image
		if(!mainMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainMultipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainMultipartFile);
		}
		
		//Save extra images
		if(extraMultipartFiles.length > 0) {
			for(MultipartFile multipartFile : extraMultipartFiles) {
				String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
				if(multipartFile.isEmpty()) {
					continue;
				}
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
		
	}

	static void setNewExtraImageName(MultipartFile[] extraMultipartFiles, Product product) {
		if(extraMultipartFiles.length > 0) {
			for(MultipartFile multipartFile : extraMultipartFiles) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if(!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}

	static void setMainImageName(MultipartFile mainMultipartFile, Product product) {
		if(!mainMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainMultipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	
}
