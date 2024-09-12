<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: 14A
  Date: 2024-08-26
  Time: 오후 4:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>반품승인</title>
    <!-- TODO : 제목 과 스타일 영역 -->
    <style>
        .thead-light > tr > th {
            text-align: center;
            vertical-align: middle !important;
        }

        tbody > tr > td {
            text-align: center;
            vertical-align: middle !important;
        }

        .nav-link:hover {
            cursor: pointer;
        }

    </style>
</head>

<body>
<!-- 메뉴바 -->
<jsp:include page="../common/header.jsp"/>
<div style="height: 90px"></div>
<div id="content-wrap-area">

    <nav class="navbar navbar-expand-sm bg-dark navbar-dark justify-content-center">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="productInsertForm.do">상품 등록</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="adminQuantity.do">상품 재고수정</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="admin.do">주문조회</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" id="adminPayment" href="adminPayment.do">결제취소</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" onclick="location.reload();">반품 승인</a>
            </li>


            <li class="nav-item">
                <a class="nav-link" id="changeStatus" href="adminRefund.do">배송상태 변경</a>
            </li>
            <li class="nav-item">
                <div class="icon-header-item cl2 hov-cl1 trans-04 p-r-11 p-l-10 icon-header-noti"
                     id="qa-notify"
                     data-notify="0">
                    <a class="nav-link" style="font-size: 16px; vertical-align: middle !important; margin-top: 3px;"
                       id="viewQaList" href="adminQaList.do">Q&A 미답변</a>
                </div>
            </li>
        </ul>
    </nav>

    <br><br>
    <table class="table table-hover" style="width: 90%; margin: auto">
        <thead class="thead-light">
        <tr>
            <th class="col-2">주문번호<br>주문일자</th>
            <th class="col-3">상품명</th>
            <th class="col-1">결제방식</th>
            <th class="col-1">총 결제금액</th>
            <th class="col-1">결제상태</th>
            <th class="col-1">주문상태</th>
            <th class="col-1">반품처리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="listReturns" items="${listReturns}">
            <tr>
                <td>${listReturns.id}<br>${listReturns.createdAt}</td>
                <td>${listReturns.productName}</td>
                <td>${listReturns.paymentType == 'vbank' ? '무통장입금' : '카드결제'}</td>
                <td>${listReturns.price}원</td>
                <td>${listReturns.valid == 'y' ? '결제완료' : '환불완료'}</td>
                <td>${listReturns.status}</td>
                <td>
                    <c:if test="${listReturns.status ne '환불완료'}">
                        <input class="btn btn-light" type="button" value="환불처리" onclick="ordersReturns(this);">
                    </c:if>
                </td>
                <input type="hidden" name="returnsId" value="${listReturns.id}">
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>
<!-- 푸터 -->
<jsp:include page="../common/footer.jsp"/>
</body>

<script>

    function ordersReturns(button) {

        // 클릭된 버튼의 부모 tr 설정
        const row = $(button).closest('tr');

        // ordersId 값을 가져오기
        const returnsId = row.find('input[name="returnsId"]').val();

        $.ajax({
            url: 'ordersRefund.do', // 컨트롤러에서 갯수를 가져오는 URL
            type: 'POST',
            data: {id: returnsId},
            dataType: 'json',
            success: function (response) {
                if (response > 0) {
                    alert("반품처리에 성공했습니다.");
                    location.reload();
                }
            },
            error: function (xhr, status, error) {
                alert("반품 처리에 실패했습니다.");
            }
        });
    }

    $(document).ready(function () {

        updateQaCount();

    });

    function updateQaCount() {
        $.ajax({
            url: 'qaCount.do', // 컨트롤러에서 갯수를 가져오는 URL
            type: 'GET',
            dataType: 'json',
            success: function (response) {
                if (response && response.count !== undefined) {
                    $('#qa-notify').attr('data-notify', response.count);
                } else {
                    $('#qa-notify').attr('data-notify', '0'); // 갯수가 없을 경우 0으로 설정
                }
            },
            error: function (xhr, status, error) {
                console.error('QA 갯수 가져오는 데 실패했습니다.', error);
                $('#qa-notify').attr('data-notify', '0'); // 오류 발생 시 0으로 설정
            }
        });
    }

</script>
</html>