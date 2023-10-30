/**
 * 
 */
import MeetingAssignCall from './meeting/stat_pack.js';
import MeetingScheduleCall from './meeting/schedule_pack.js';
import MeetingInfoCall from './meeting/info_pack.js';
import MeetingAttendeeCall from './meeting/attendee_pack.js';
import MeetingStatCall from './meeting/assign_pack.js';
import MeetingFileCall from './meeting/file_pack.js';
import MeetingNoteCall from './meeting/note_pack.js';
import MeetingReportCall from './meeting/report_pack.js';

const MeetingCall = {
  Get: {
	  ...MeetingAssignCall.Get,
	  ...MeetingScheduleCall.Get,
	  ...MeetingInfoCall.Get,
	  ...MeetingAttendeeCall.Get,
	  ...MeetingStatCall.Get,
	  ...MeetingFileCall.Get,
	  ...MeetingNoteCall.Get,
	  ...MeetingReportCall.Get,
  },
  Post: {
	  ...MeetingAssignCall.Post,
	  ...MeetingScheduleCall.Post,
	  ...MeetingInfoCall.Get,
	  ...MeetingAttendeeCall.Post,
	  ...MeetingStatCall.Post,
	  ...MeetingFileCall.Post,
	  ...MeetingNoteCall.Post,
	  ...MeetingReportCall.Post,
  },
  Put: {
	  ...MeetingAssignCall.Put,
	  ...MeetingScheduleCall.Put,
	  ...MeetingInfoCall.Get,
	  ...MeetingAttendeeCall.Put,
	  ...MeetingStatCall.Put,
	  ...MeetingFileCall.Put,
	  ...MeetingNoteCall.Put,
	  ...MeetingReportCall.Put,
  },
  Delete: {
	  ...MeetingAssignCall.Delete,
	  ...MeetingScheduleCall.Delete,
	  ...MeetingInfoCall.Get,
	  ...MeetingAttendeeCall.Delete,
	  ...MeetingStatCall.Delete,
	  ...MeetingFileCall.Delete,
	  ...MeetingNoteCall.Delete,
	  ...MeetingReportCall.Delete,
  },
};

export default MeetingCall;