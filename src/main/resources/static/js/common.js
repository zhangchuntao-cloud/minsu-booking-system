/* ==========================================================================
   云栖民宿预订管理系统 —— 公共脚本
   包含：异步请求封装、浮动提示、表单校验、轮播图、图片画廊、分页交互
   全部使用原生 JavaScript 编写，不依赖任何第三方库
   ========================================================================== */

(function (window, document) {
    'use strict';

    /* ==================== 异步请求封装 ==================== */

    /**
     * 统一的异步请求方法。
     * 后端所有接口都返回 {code, msg, data} 结构，这里统一解析：
     * code 为 200 视为成功，401 自动跳转登录页，其余弹出错误提示。
     *
     * @param {string} url    请求地址
     * @param {object} params 请求参数（将以表单形式提交）
     * @param {object} options 可选配置 {silent: 是否静默处理错误}
     * @returns {Promise} 成功时 resolve 整个响应对象
     */
    function post(url, params, options) {
        var opts = options || {};
        var body = new URLSearchParams();
        if (params) {
            Object.keys(params).forEach(function (key) {
                var value = params[key];
                if (value !== null && value !== undefined) {
                    body.append(key, value);
                }
            });
        }
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: body.toString()
        }).then(function (response) {
            return response.json();
        }).then(function (result) {
            if (result.code === 200) {
                return result;
            }
            if (result.code === 401) {
                toast('登录状态已失效，即将跳转登录页', 'error');
                setTimeout(function () {
                    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                }, 1200);
                throw new Error(result.msg);
            }
            if (!opts.silent) {
                toast(result.msg || '操作失败', 'error');
            }
            throw new Error(result.msg || '操作失败');
        }).catch(function (error) {
            if (error instanceof TypeError) {
                // 网络异常或响应不是合法 JSON
                toast('网络异常，请检查连接后重试', 'error');
            }
            throw error;
        });
    }

    /** GET 方式的异步请求 */
    function get(url, params, options) {
        var query = new URLSearchParams(params || {}).toString();
        var fullUrl = query ? url + '?' + query : url;
        return fetch(fullUrl, {
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        }).then(function (response) {
            return response.json();
        }).then(function (result) {
            if (result.code === 200) {
                return result;
            }
            if (!(options && options.silent)) {
                toast(result.msg || '请求失败', 'error');
            }
            throw new Error(result.msg || '请求失败');
        });
    }

    /* ==================== 浮动提示 ==================== */

    var toastTimer = null;

    /**
     * 显示浮动提示。
     * @param {string} message 提示文字
     * @param {string} type    success / error / info
     */
    function toast(message, type) {
        var el = document.getElementById('toast');
        if (!el) {
            el = document.createElement('div');
            el.id = 'toast';
            document.body.appendChild(el);
        }
        el.textContent = message;
        el.className = 'show ' + (type || 'info');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () {
            el.className = '';
        }, 2600);
    }

    /* ==================== 二次确认 ==================== */

    /**
     * 使用自定义模态框做二次确认，替代浏览器原生 confirm，
     * 视觉风格与全站保持一致。
     */
    function confirmAction(message, onConfirm, confirmText) {
        var mask = document.createElement('div');
        mask.className = 'modal-mask show';
        mask.innerHTML =
            '<div class="modal" style="max-width:400px">' +
            '  <div class="modal-head">操作确认<span class="modal-close">&times;</span></div>' +
            '  <div class="modal-body" style="color:#4b5563;line-height:1.8">' + message + '</div>' +
            '  <div class="modal-foot">' +
            '    <button type="button" class="btn btn-outline" data-role="cancel">取消</button>' +
            '    <button type="button" class="btn btn-danger" data-role="ok">' + (confirmText || '确定') + '</button>' +
            '  </div>' +
            '</div>';
        document.body.appendChild(mask);

        function close() {
            document.body.removeChild(mask);
        }

        mask.querySelector('[data-role="cancel"]').onclick = close;
        mask.querySelector('.modal-close').onclick = close;
        mask.querySelector('[data-role="ok"]').onclick = function () {
            close();
            onConfirm();
        };
        mask.addEventListener('click', function (e) {
            if (e.target === mask) {
                close();
            }
        });
    }

    /* ==================== 表单校验 ==================== */

    var RULES = {
        required: function (value) {
            return value !== null && String(value).trim() !== '';
        },
        phone: function (value) {
            return /^1[3-9]\d{9}$/.test(value);
        },
        email: function (value) {
            return value === '' || /^[\w.-]+@[\w-]+(\.[\w-]+)+$/.test(value);
        },
        username: function (value) {
            return /^[a-zA-Z][a-zA-Z0-9_]{3,19}$/.test(value);
        },
        password: function (value) {
            return value.length >= 6 && value.length <= 20;
        },
        number: function (value) {
            return !isNaN(parseFloat(value)) && isFinite(value);
        },
        positive: function (value) {
            return parseFloat(value) > 0;
        }
    };

    /**
     * 校验单个表单控件。
     * 控件通过 data-rule 属性声明规则，多个规则用竖线分隔，如 data-rule="required|phone"。
     */
    function validateField(field) {
        var ruleText = field.getAttribute('data-rule');
        if (!ruleText) {
            return true;
        }
        var value = field.value;
        var label = field.getAttribute('data-label') || '该项';
        var rules = ruleText.split('|');
        var messages = {
            required: '请填写' + label,
            phone: label + '格式不正确',
            email: label + '格式不正确',
            username: '账号需以字母开头，长度 4-20 位',
            password: '密码长度需为 6-20 位',
            number: label + '必须是数字',
            positive: label + '必须大于 0'
        };
        for (var i = 0; i < rules.length; i++) {
            var rule = rules[i].trim();
            var checker = RULES[rule];
            if (checker && !checker(value, field)) {
                return messages[rule] || (label + '填写有误');
            }
        }
        return true;
    }

    /** 为带有 data-rule 的控件绑定失焦即时校验 */
    function bindValidation(scope) {
        var root = scope || document;
        root.querySelectorAll('[data-rule]').forEach(function (field) {
            field.addEventListener('blur', function () {
                showFieldError(field, validateField(field));
            });
            field.addEventListener('input', function () {
                if (field.dataset.touched === '1') {
                    showFieldError(field, validateField(field));
                }
            });
        });
    }

    /** 显示或清除单个字段的错误提示 */
    function showFieldError(field, result) {
        field.dataset.touched = '1';
        var holder = field.parentNode.querySelector('.form-error');
        if (result === true) {
            field.style.borderColor = '';
            if (holder) {
                holder.remove();
            }
            return;
        }
        field.style.borderColor = '#dc2626';
        if (!holder) {
            holder = document.createElement('div');
            holder.className = 'form-error';
            field.parentNode.appendChild(holder);
        }
        holder.textContent = result;
    }

    /**
     * 校验整个表单，返回是否全部通过。
     * 用于表单提交前的统一拦截，避免无效数据发往服务端。
     */
    function validateForm(form) {
        var valid = true;
        var firstError = null;
        form.querySelectorAll('[data-rule]').forEach(function (field) {
            var result = validateField(field);
            showFieldError(field, result);
            if (result !== true && valid) {
                valid = false;
                firstError = field;
            }
        });
        if (firstError) {
            firstError.focus();
            firstError.scrollIntoView({behavior: 'smooth', block: 'center'});
        }
        return valid;
    }

    /* ==================== 首页轮播图 ==================== */

    /**
     * 初始化首页轮播。
     * 每 5 秒自动切换一张，鼠标移入暂停，点击圆点可手动切换。
     */
    function initCarousel() {
        var hero = document.querySelector('.hero');
        if (!hero) {
            return;
        }
        var slides = hero.querySelectorAll('.hero-slide');
        var dots = hero.querySelectorAll('.hero-dot');
        if (slides.length < 2) {
            return;
        }
        var index = 0;
        var timer = null;

        function show(next) {
            slides.forEach(function (slide, i) {
                slide.classList.toggle('active', i === next);
            });
            dots.forEach(function (dot, i) {
                dot.classList.toggle('active', i === next);
            });
            index = next;
        }

        function start() {
            timer = setInterval(function () {
                show((index + 1) % slides.length);
            }, 5000);
        }

        function stop() {
            clearInterval(timer);
        }

        dots.forEach(function (dot, i) {
            dot.addEventListener('click', function () {
                stop();
                show(i);
                start();
            });
        });

        hero.addEventListener('mouseenter', stop);
        hero.addEventListener('mouseleave', start);
        show(0);
        start();
    }

    /* ==================== 房源图片画廊 ==================== */

    /**
     * 详情页图片画廊：点击缩略图切换主图。
     */
    function initGallery() {
        var main = document.getElementById('galleryMain');
        if (!main) {
            return;
        }
        document.querySelectorAll('.gallery-thumb').forEach(function (thumb) {
            thumb.addEventListener('click', function () {
                var img = thumb.querySelector('img');
                if (!img) {
                    return;
                }
                main.src = img.dataset.large || img.src;
                document.querySelectorAll('.gallery-thumb').forEach(function (t) {
                    t.classList.remove('active');
                });
                thumb.classList.add('active');
            });
        });
    }

    /* ==================== 收藏按钮 ==================== */

    /**
     * 绑定收藏按钮：点击后调用接口切换状态，并同步按钮文案与样式。
     */
    function bindFavoriteButtons() {
        document.querySelectorAll('[data-action="favorite"]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var houseId = btn.dataset.houseId;
                post('/favorite/toggle', {houseId: houseId}).then(function (result) {
                    var favorited = result.data.favorited;
                    btn.classList.toggle('active', favorited);
                    btn.innerHTML = favorited ? '♥ 已收藏' : '♡ 收藏';
                    toast(result.msg, 'success');
                }).catch(function () {
                    /* 错误提示已在 post 中统一处理 */
                });
            });
        });
    }

    /* ==================== 分页跳转 ==================== */

    /**
     * 跳转到指定页码。
     * 直接在当前地址上替换 pageNum 参数，这样筛选条件等其它参数
     * 会被完整保留，不必在每个分页链接里重复拼接。
     */
    function goPage(page) {
        var url = new URL(window.location.href);
        url.searchParams.set('pageNum', page);
        window.location.href = url.toString();
    }

    /**
     * 绑定分页输入框的跳转按钮。
     */
    function bindPageJump() {
        document.querySelectorAll('[data-role="page-jump"]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var input = btn.parentNode.querySelector('input');
                var page = parseInt(input.value, 10);
                var total = parseInt(input.dataset.total, 10);
                if (isNaN(page) || page < 1 || page > total) {
                    toast('请输入 1 到 ' + total + ' 之间的页码', 'error');
                    return;
                }
                goPage(page);
            });
        });
    }

    /* ==================== 日期工具 ==================== */

    /**
     * 把日期对象格式化为 yyyy-MM-dd 字符串。
     * 注意不能直接用 toISOString()，它按 UTC 输出，东八区会差一天。
     */
    function formatDate(date) {
        var y = date.getFullYear();
        var m = String(date.getMonth() + 1).padStart(2, '0');
        var d = String(date.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + d;
    }

    /** 计算两个日期之间的天数差 */
    function daysBetween(startText, endText) {
        var start = new Date(startText + 'T00:00:00');
        var end = new Date(endText + 'T00:00:00');
        return Math.round((end - start) / 86400000);
    }

    /* ==================== 页面初始化 ==================== */

    document.addEventListener('DOMContentLoaded', function () {
        bindValidation(document);
        initCarousel();
        initGallery();
        bindFavoriteButtons();
        bindPageJump();
    });

    /* ==================== 对外暴露 ==================== */
    window.Minsu = {
        post: post,
        get: get,
        toast: toast,
        confirm: confirmAction,
        validateForm: validateForm,
        formatDate: formatDate,
        daysBetween: daysBetween,
        goPage: goPage
    };

})(window, document);
