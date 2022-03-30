$(document).ready(function(){
 			$("#logoutLink").on("click", function(e){
 				e.preventDefault();
 				document.logoutForm.submit();
 			});
 			customizeDropDownMenu();
 		});
 		
 		// select dropdown
 function customizeDropDownMenu(){
	$(".navbar .dropdown").hover(
		function(){
			$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideDown();
		},
		function(){
			$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
		}
	);
	$("#dropdown-account").click(function(){
		location.href = this.href;
	})
}