const path = require('path');
const resourcesPath = 'src/main/webapp/resources';
const commonBasePath = 'front-end-assets/js/ewp/page';
const commonConfig = {
    mode: 'production',
    resolve: {
        alias: {
            '/resources': path.resolve(__dirname, resourcesPath),
        }
    },
};

const createConfig = (entryFile) => {
    const entryName = path.basename(entryFile, '.js');
    const outputDir = path.join(path.dirname(entryFile), 'dist');

    return Object.assign({}, commonConfig, {
        entry: path.resolve(__dirname, resourcesPath, commonBasePath, entryFile),
        output: {
            filename: `${entryName}.bundle.js`,
            path: path.resolve(__dirname, resourcesPath, commonBasePath, outputDir)
        }
    });
};
const assign_post_module = 'meeting/assign/assign_post.js';
const assign_status_list_module = 'meeting/assign/assign_status_list.js';
const assign_status_timetable_module = 'meeting/assign/assign_status_timetable.js';
const authority_manage_module = 'manage/admin/authority_manage.js';
const approval_manage_module = 'manage/admin/approval_manage.js'
const approval_request_module = 'meeting/assign/assign_approval.js';
const history_manage_module = 'manage/assign/history_searcher.js';
const security_agreement_module = 'meeting/meeting_security-agreement.js';
const meeting_space_module = 'meeting/space/core-module.js';
const modules = [
	assign_post_module,
	assign_status_list_module,
	assign_status_timetable_module,
	authority_manage_module,
    approval_manage_module,
    approval_request_module,
	history_manage_module,
	security_agreement_module,
	meeting_space_module,
].map(createConfig);

module.exports = modules;