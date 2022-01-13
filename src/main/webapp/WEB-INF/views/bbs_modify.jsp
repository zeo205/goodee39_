<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
</head>
<body>
	<h1>글 수정</h1>
	<form:form modelAttribute="BBSVO" action="${pageContext.request.contextPath}/bbs/modify_result">
		<ul>
			<li><label for="title">제목 : </label><form:input path="title"/></li>
			<li><form:textarea path="content" cols="50" rows="10"/></li>
			<form:hidden path="num"/>
			<form:hidden path="filelist"/>
		</ul>
	</form:form>
	<c:forEach var="file" items="${filelist}">
	<div class="file-item">
		<a href="${pageContext.request.contextPath}/downloadFile/${file.localName}/${file.serverName}">${file.localName}</a>
		<button data-num="${file.num}" class="deleteFile">삭제</button>
	</div>
	</c:forEach>
	<button class="alldelete" data-bnum="${BBSVO.num}">전체삭제</button><br>
	<label for="upload">파일 추가 :</label><input type="file" id="upload" name="upload" multiple>
	<button type="button" id="modify">수정</button>
	
	<script type="text/javascript">
	$(function(){
		let flagSingle = false;
		let flagAll = false;
		let bnum = 0;
		let num = [];
		
		$(".deleteFile").click(function(){
			flagSingle = true;
			num.push({num : this.dataset.num});
			$(this).parent().remove();
			//console.log(num);
		});
		
		$(".alldelete").click(function(){
			flagAll = true;
			bnum = this.dataset.bnum;
			$(".file-item").remove();
		});
		
		
		$("#modify").click(function(){
			// 파일 삭제
			if(flagAll){
				$.ajax({
					url : '${pageContext.request.contextPath}/bbs/deleteFileAll',
					data : JSON.stringify({bnum : bnum}),
					type : "post",
					contentType:"application/json; charset=utf-8",
					datatype : "json",
					success: function(result){
						console.log(JSON.stringify(result));
					}	
				});	
			}else if(flagSingle){
				$.ajax({
					url : '${pageContext.request.contextPath}/bbs/deleteFile',
					data : JSON.stringify(num),
					type : "post",
					contentType:"application/json; charset=utf-8",
					datatype : "json",
					success: function(result){
						console.log(JSON.stringify(result));
					}
				});		
			}
			
			const formData = new FormData();
			const $upload = $("#upload");
			let files = $upload[0].files;
			
			//console.log(files);
			// 파일 추가
			if(files.length != 0){
				for (var i = 0; i < files.length; i++) {
					formData.append("uploadFile", files[i])	
				}
				
				$.ajax({
					url : '${pageContext.request.contextPath}/bbs/uploadfile',
					processData : false,
					contentType : false,
					data : formData,
					type : "post",
					datatype : "json",
					success: function(result){
						//console.log(JSON.stringify(result));
						$("#filelist").val(JSON.stringify(result));
						console.log(result);
						//$("#BBSVO").submit();
						$("#BBSVO").submit();
					}
				});
			}else{
				$("#BBSVO").submit();
			}
			
			
		});
	});
	</script>
</body>
</html>