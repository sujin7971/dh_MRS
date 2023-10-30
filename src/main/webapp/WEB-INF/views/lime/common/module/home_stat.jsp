<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 
* 홈페이지 통계모듈
 --%>
<!DOCTYPE HTML>
<div class="statSection margin-top-20">
    <div class="titDiv align-items-center">
        <div class="pageTit"><span>나의 전자회의 성과 </span><span>(누적통계)</span></div>
        <div class="help" id="statHelpBtn"></div>
    </div>

    <div class="container ">
        <div class="detail">
            <article class="paper">
                <div class="item">A4 용지 절약</div>
                <div class="answer w-50" data-stat="pageReduction"></div>
            </article>
            <article class="money">
                <div class="item">용지비용 절약</div>
                <div class="answer w-50" data-stat="costReduction"></div>
            </article>
            <article class="co2">
                <div class="item">온실가스 감소</div>
                <div class="answer w-50" data-stat="gasReduction"></div>
            </article>
            <article class="water">
                <div class="item">물 절약</div>
                <div class="answer w-50" data-stat="waterReduction"></div>
            </article>
        </div>
    </div>
</div>
<script type="module" src="/resources/front-end-assets/js/ewp/page/common/home_stat.js"></script>
