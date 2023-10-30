import {Dom, eventMixin, ModalService} from '/resources/core-assets/essential_index.js';
/**
 * text형 input에 jquery ui의 datepicker를 통한 날짜 입력을 돕는 클래스입니다.
 * 프로젝트 설계에 따라 모달을 통한 날짜 선택 기능만 제공합니다.
 * jquery, jquery-ui, moment 라이브러리가 사용할 템플릿 페이지에 로드되어야 합니다.
 * 
 * @author mckim
 * @version 2.0
 * @since 2023. 07. 06
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
const DATEPICKER_MODAL_TEMPLATE = 
`
<div class="modal" data-modal="datePicker">
	<div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody flex-direction-column align-items-center">
               <div class="calendarDiv" data-modal-calendar></div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" data-modal-action="close" class="btn btn-md btn-silver">취 소</button>
                <button type="button" data-modal-action="ok" class="btn btn-md btn-blue">확 인</button>
            </div>
        </div>
    </div>
</div>
`;
const Modal = ModalService.getModalModel();
class DatepickerModal extends Modal {
	constructor(data = {}) {
		super(data);
		const $datepicker = this.modalElement.querySelector("[data-modal-calendar]");
		const _datepicker = $($datepicker).datepicker({dateFormat: "yy-mm-dd"});
		this._datepicker = _datepicker;
	}
	show(options = {}){
		const {
			selectedDate = moment().format("YYYY-MM-DD"),
			minDate,
			maxDate,
		} = options;
		if (minDate) {
            this._datepicker.datepicker("option", "minDate", options.minDate);
        }
        if (maxDate) {
            this._datepicker.datepicker("option", "maxDate", options.maxDate);
        }
        if (moment(selectedDate).isValid()) {
            this._datepicker.datepicker("setDate", selectedDate);
        }else{
			this._datepicker.datepicker("setDate", moment().format("YYYY-MM-DD"));
		}
		return super.show();
	}
	getSelectedDate(){
		const newdate = this._datepicker.datepicker("getDate");
        const formattedDate = moment(newdate).format("YYYY-MM-DD");
        return formattedDate;
	}
}
const registerDatepickerModal = ($modal) => {
	const modalInstance = new DatepickerModal({
		modalElement: $modal,
		autoDestroy: false,
	});
	ModalService.add(modalInstance);
	return modalInstance;
}
class DateInputHelper {
    constructor(options = {}) {
    	const {
    	} = options;
    	const modal = ModalService.get("datePicker") || (() => {
			const $modal = Dom.createFromString(DATEPICKER_MODAL_TEMPLATE);
    		Dom.getElement("body").appendChild($modal);
    		return registerDatepickerModal($modal);
		})();
    	this.modal = modal;
        this.inputs = [];
    }
    // selector를 통해 datepicker기능을 제공할 input을 등록합니다.
    addInput(selector, options = {}) {
        const input = Dom.getElement(selector);
        if (input) {
            input.value = options.defaultValue || input.value;
            input.onclick = async () => {
                const {
					minDate,
					maxDate,
				} = options;
                const {action} = await this.modal.show({
					minDate: minDate,
					maxDate: maxDate,
					selectedDate: input.value,
				});
                if (action == "OK") {
                    const selectedDate = this.modal.getSelectedDate();
                    input.value = selectedDate;
                    this.trigger("change", {name: input.name, value: formattedDate});
                }
            };
            this.inputs.push(input);
            this._updatePeriodOptions();
        }
        input.style.cursor = "pointer";
        input.onkeydown = (event) => {
			event.preventDefault();
			return false;
		}
        return this;
    }
	//addInput이 두번 호출되는경우, 기간 입력 모드로 전환됩니다.
    _updatePeriodOptions() {
        if (this.inputs.length == 2) {
            this.inputs[0].onclick = async () => {
                const input = this.inputs[0];
                const otherInput = this.inputs[1];
                const otherDate = otherInput.value;
                const {action} = await this.modal.show({
					selectedDate: input.value,
				});
                if (action == "OK") {
                    const selectedDate = this.modal.getSelectedDate();
                    if(moment(selectedDate).isAfter(moment(otherDate))) {
                        otherInput.value = selectedDate;
                        this.trigger("change", {name: otherInput.name, value: selectedDate});
                    }
                    input.value = selectedDate;
                    this.trigger("change", {name: input.name, value: selectedDate});
                }
            };
            this.inputs[1].onclick = async () => {
                const input = this.inputs[1];
                const otherInput = this.inputs[0];
                const otherDate = otherInput.value;
                const {action} = await this.modal.show({
					selectedDate: input.value,
				});
                if (action == "OK") {
                    const selectedDate = this.modal.getSelectedDate();
                    if(moment(selectedDate).isBefore(moment(otherDate))) {
                        otherInput.value = selectedDate;
                        this.trigger("change", {name: otherInput.name, value: selectedDate});
                    }
                    input.value = selectedDate;
                    this.trigger("change", {name: input.name, value: selectedDate});
                }
            };
        }
    }
}
Object.assign(DateInputHelper.prototype, eventMixin);
export default DateInputHelper;