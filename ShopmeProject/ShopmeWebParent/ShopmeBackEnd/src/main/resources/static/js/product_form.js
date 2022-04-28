	dropdownBrands = $("#brand")
	dropdownCategories =$("#category")
	
	$(document).ready(function(){
	
		$("#shortDescription").richText();
		$("#fullDescription").richText();
		
		dropdownBrands.change(function(){
			dropdownCategories.empty();
			getCategories();
		});
		getCategories();
		
		$("input[name ='extraImage']").each(function(index){
			$(this).change(function(){
				showExtraImageThumbnail(this, index);
			});
		});
		
	})
	
	function showExtraImageThumbnail(fileInput, index){
		var file = fileInput.files[0];
		//FileReader(File file)	It gets filename in file instance. 
		//It opens the given file in read mode. If file doesn't exist, it throws FileNotFoundException.
		var reader = new FileReader();
		reader.onload = function(e){
			$("#extraThumbnail" + index).attr("src", e.target.result);
		};
		
		reader.readAsDataURL(file);
		addNextExtraImageSection(index + 1);
	}
	
	function addNextExtraImageSection(index){
		htmlExtraImage = `
		<div class="col border border-2 m-2 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}"><label>Extra Image #${index + 1}</label></div>
			<div class ="m-2 mb-3">
				<img id ="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
					src = "${defaultImageThumbnailSrc}">
			</div>
			<div>
				<input type="file" class="form-control" name="extraImage"
					onchange="showExtraImageThumbnail(this, ${index})"
					accept="image/png, image/jpeg">
			</div>
		</div>
		`
		htmlLinkRemove =`
		 <a class="fas fa-times-circle fa-2x float-right" 
		 	style="color:#333333;"
		 	href="javascript:removeExtraImage(${index} - 1)"
		 	title="Remove this image"></a>;
		`;
		
		$("#divProductImages").append(htmlExtraImage);
		$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
	}
	
	function removeExtraImage(index){
		$("#divExtraImage" + index).remove();
	}
	
	function getCategories(){
		brandId = dropdownBrands.val();
		url = brandModuleURL + "/" + brandId + "/categories";
		$.get(url, function(responseJson){
			$.each(responseJson, function(index, category){
				$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
			})
		})
	}
	
	function checkUnique(form){
		productName = $("#name").val();
		productId = $("#id").val();
		
		csrfValue = $("input[name = '_csrf']").val()
	
		url = "[[@{/products/check_unique}]]"
		params = {id : productId, name: productName, _csrf: csrfValue};
		
		$.post(url, params, function(response){
			if(response == "OK"){
				form.submit();
			}else if(response == "Duplicate"){
				showWarningModal("There is another category having the same name: " + productName)
			}else{
				showErrorModal("Unknown response from server");
			}
		}).fail(function(){
			showErrorModal("Could not connect to the server")
		});
		
		return false;
	}