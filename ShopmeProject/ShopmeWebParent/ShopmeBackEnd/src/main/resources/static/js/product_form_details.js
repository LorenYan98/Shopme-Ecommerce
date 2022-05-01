function addNextDetailSection(){
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	nextDivDetailId = divDetailsCount;
	
	htmlDetailSection = `
	<div class = "form-inline" id="divDetail${nextDivDetailId}">
		<label class ="m-3">Name: </label>
		<input type="text" class="form-control w-25" name="detailNames" maxlength="256">
		<label class ="m-3">Value: </label>
		<input type="text" class="form-control w-25" name="detailValues " maxlength="256">
	</div>
	`;
	$("#divProductDetails").append(htmlDetailSection);
	
	previousDivDetailSection = allDivDetails.last();
	previousDivDetailId = previousDivDetailSection.attr("id");
	
	htmlLinkRemove =`
		 <a class="fas fa-times-circle fa-2x float-right ml-2" 
		 	style="color:#333333;"
		 	href="javascript:removeDetailSectionById('${previousDivDetailId}')"
		 	title="Remove this detail"></a>
		`;
	
	previousDivDetailSection = allDivDetails.last();
	previousDivDetailSection.append(htmlLinkRemove);
}

function removeDetailSectionById(id){
	$("#" + id).remove();
}