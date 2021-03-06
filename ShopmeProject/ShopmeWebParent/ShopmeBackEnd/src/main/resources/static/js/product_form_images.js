
	var extraImageCount = 0;
	
	$(document).ready(function(){
	
		$("input[name ='extraImage']").each(function(index){
			extraImageCount++;
			
			$(this).change(function(){
				if(!checkFileSize(this)){
					return;
				}else{
					showExtraImageThumbnail(this, index);
				}
			});
		});
		
		$("a[name='linkRemoveExtraImage']").each(function(index){
			$(this).click(function(){
				removeExtraImage(index);
			});
		});
	});
	
	function showExtraImageThumbnail(fileInput, index){
		var file = fileInput.files[0];
		
		fileName = file.name;
		imageNameHiddenField = $("#imageName" + index);
		
		if(imageNameHiddenField.length){
			imageNameHiddenField.val(fileName);
		}
		
		
		//FileReader(File file)	It gets filename in file instance. 
		//It opens the given file in read mode. If file doesn't exist, it throws FileNotFoundException.
		var reader = new FileReader();
		reader.onload = function(e){
			$("#extraThumbnail" + index).attr("src", e.target.result);
		};
		
		reader.readAsDataURL(file);
		if(index >= extraImageCount - 1){
			addNextExtraImageSection(index + 1);
		}
		
	}
	
	function addNextExtraImageSection(index){
		htmlExtraImage = `
		<div class="col-3 border border-2 m-2 p-2" id="divExtraImage${index}">
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
		extraImageCount++;
	}
	
	function removeExtraImage(index){
		$("#divExtraImage" + index).remove();
	}
	