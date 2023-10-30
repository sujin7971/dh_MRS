/**
 * 
 */
import Textarea from '../../shape/basic/Textarea.js';
import {textareaMixin} from '../../mixin/tool/basicMixin.js';
const Textbox = new Textarea({
	level: 2,
});
Object.assign(Textbox, textareaMixin);
Textbox.setName("textbox");
Textbox.setTitle("텍스트상자");
export default Textbox;