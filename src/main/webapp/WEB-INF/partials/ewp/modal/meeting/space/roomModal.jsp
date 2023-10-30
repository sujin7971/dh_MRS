<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- modal 참석자 아이콘 안내창 --%>
<div id="userIndexModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="xBtn" data-modal-btn="CLOSE"></div>
            <div class="modalTitle">참석자 구분</div>
            <div class="modalBody flex-direction-column align-items-center">
                <div class="f1">
                    <div class="row">
                        <%--<div class="item"><i class="fas fa-user-tie"></i></div> --%>
                        <div class="answer">
                            <span><b>회의 진행자 :</b> 회의진행에 관련된 모든 권한을 가집니다.</span>
                        </div>
                    </div>
                    <div class="row">
                        <%--<div class="item"><i class="fas fa-user-edit"></i></div> --%>
                        <div class="answer">
                            <span><b>보조 진행자 :</b> 회의 진행자가 참석자들 중에서 직접 지정하며, 회의 진행자와 동등한 권한을 가집니다.</span>
                        </div>
                    </div>
                    <div class="row">
                        <%--<div class="item"><i class="fas fa-user-check"></i></div> --%>
                        <div class="answer">
                            <span><b>참석자 :</b> 회의 진행자로부터 필수참석을 요청받은 사용자입니다.</span>
                        </div>
                    </div>
                    <%-- <div class="row">
                        <div class="item"><i class="fas fa-user-alt"></i></div>
                        <div class="answer">
                            <span><b>참관자 :</b> 회의 진행자로부터 선택참석을 요청받은 사용자입니다.</span>
                        </div>
                    </div> --%>
                    <%--
                    <div class="row">
                        <div class="item"><i class="fas fa-user-tag"></i></div>
                        <div class="answer">
                            <span><b>외부참석자 :</b> 외부인 참석자입니다. 회의진행자 또는 보조 진행자가 '참석자추가' 기능을 통해 입장시킵니다.</span>
                        </div>
                    </div>
                    --%>
                </div>
            </div>
        </div>
    </div>
</div>
<%-- modal 보안등급 안내 --%>
<div id="securityInfoModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">보안관련 안내</div>
            <div class="modalBody  flex-direction-column">
                <div class="infoMentDiv">
                    <p>일반회의 : 일반회의는 회의종료 후, 해당 회의의 정보 및 자료의 공개범위를 지정할 수 있습니다.</p>
                </div>
                <div class="infoMentDiv">
                    <p>기밀회의 : 기밀회의로 설정되면 회의 종료 후 모든 자료 (첨부파일, 회의진행 중 생성된 모든 파일, 참석자목록) 가 파기되어 회의 진행자 까지도 회의관련된 자료의 열람이 불가합니다.
                        <br>단, 회의 자체의 히스토리(일시,장소,제목,회의 진행자)는 보관됩니다.</p>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<%-- 참석자 정보 --%>
<div id="userInfoModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="xBtn" data-modal-btn="CANCEL"></div>
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody flex-direction-column align-items-center">
                <div id="userPic" class="userPic"></div>
                <div class="width100p">
                    <p class="uDivision"></p>
                    <p class="uNameGrade"></p>
                </div>
                <div class="f2">
                    <p><span>사번</span><span class="uComNum"></span></p>
                    <p><span>모바일</span><span class="uPhonNum d-inline"></span></p>
                    <p><span>이메일</span><span class="uEmail"></span></p>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- modal 회의종료시간 10분전, 5분전 얼럿 -->
<div id="timeCountModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <b>회의종료 <span data-timer="end"></span>분 전 입니다.</b><br>
                    <p>회의가 자동종료 되기전에 아래 내용을 확인해 주세요.</p>
                </div>
            </div>
            <ul class="modalInfo">
                <li>출석체크되지 않은 참석자는 불참처리됩니다.</li>
                <li>회의 보안설정에 따라 판서본과 사인은 저장되지 않을 수 있습니다.</li>
            </ul>
            <div class="modalBtnDiv">
<!--                 <button class="btn btn-md btn-silver flex1" data-modal-btn="EXT_MODAL">회의시간연장</button> -->
                <button class="btn btn-md btn-blue flex1" data-modal-btn="CONTINUE">확 인 (회의계속)</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 회의시간 연장 --%>
<div id="timeExtensionModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                    <div class="intervalInfo">
                    </div>
                    <br>
                    <div class="extInfo">
                    </div>
                </div>
            </div>
            <ul class="modalInfo">
                <li>회의 연장 시, 참석자들의 타 회의 스케줄로 인한 개별 연장 참석 가능 여부는 주최자가 판단하여 주세요.<br> 시스템은 연장회의로 인한 참석자들의 동시간대 중복 참석 상황 발생에 대해서는 관여하지 않습니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소</button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">연 장</button>
            </div>
        </div>
    </div>
</div>
