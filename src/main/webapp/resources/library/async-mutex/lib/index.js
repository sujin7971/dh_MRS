"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.tryAcquire = exports.withTimeout = exports.Semaphore = exports.Mutex = void 0;
var tslib_1 = require("tslib");
var Mutex_1 = require("./Mutex");
Object.defineProperty(exports, "Mutex", { enumerable: true, get: function () { return Mutex_1.default; } });
var Semaphore_1 = require("./Semaphore");
Object.defineProperty(exports, "Semaphore", { enumerable: true, get: function () { return Semaphore_1.default; } });
var withTimeout_1 = require("./withTimeout");
Object.defineProperty(exports, "withTimeout", { enumerable: true, get: function () { return withTimeout_1.withTimeout; } });
var tryAcquire_1 = require("./tryAcquire");
Object.defineProperty(exports, "tryAcquire", { enumerable: true, get: function () { return tryAcquire_1.tryAcquire; } });
(0, tslib_1.__exportStar)(require("./errors"), exports);
