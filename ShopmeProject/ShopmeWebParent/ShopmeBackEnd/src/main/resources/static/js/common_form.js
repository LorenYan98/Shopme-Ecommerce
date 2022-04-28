	
	//document.ready means that the code runs after the elements are created.
	// $( "#id" ) return a jQuery object containing a collection of either zero or one DOM element.
	// In the below case, $("#buttonCancel") is passed back to be further manipulated.
	// "[[@{address}]]" link to an address.
	$(document).ready(function(){
		$("#buttonCancel").on("click", function(){
			window.location = moduleURL;
		});
		$("#fileImage").change(function(){
			if(!checkFileSize(this)){
				return;
			}else{
				showImageThumbnail(this);
			}
		});
	});
	function showImageThumbnail(fileInput){
		var file = fileInput.files[0];
		//FileReader(File file)	It gets filename in file instance. 
		//It opens the given file in read mode. If file doesn't exist, it throws FileNotFoundException.
		var reader = new FileReader();
		reader.onload = function(e){
			$("#thumbnail").attr("src", e.target.result);
		};
		
		reader.readAsDataURL(file);
	}
	
	function checkFileSize(fileInput){
		fileSize = fileInput.files[0].size;
		if(fileSize > MAX_FILE_SIZE){
			fileInput.setCustomValidity("You must chosse an image less than " + max_file_size_text);
			fileInput.reportValidity();
			return false;
		}else{
			fileInput.setCustomValidity("");
			return true;
		}
	}
	
	function showModalDialog(title, message){
		$("#modalTitle").text(title);
		$("#modalBody").text(message);
		$("#modalDialog").modal();
	}
	
	function showErrorModal(message){
		showModalDialog("Error", message);
	}
	function showWarningModal(message){
		showModalDialog("Warning", message);
	}