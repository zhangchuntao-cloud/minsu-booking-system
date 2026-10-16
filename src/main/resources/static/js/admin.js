/* ==========================================================================
   后台管理公共脚本
   负责：AJAX 操作按钮绑定、模态框、图片上传、图表初始化
   ========================================================================== */

(function (window, document) {
    'use strict';

    /* ==================== 通用异步操作按钮 ==================== */

    /**
     * 绑定带 data-action 的异步操作按钮。
     * 按钮通过 data-url 指定接口地址，data-confirm 指定二次确认文案；
     * 其余 data-* 属性（如 data-id、data-status）会被自动收集为请求参数，
     * 这样模板里就不必手工拼接 JSON，也避免了在 HTML 属性中嵌套引号。
     */
    function bindActionButtons() {
        // 这些 data-* 属于控件自身配置，不作为请求参数发送
        var RESERVED = ['url', 'confirm', 'confirmText', 'action', 'reload', 'toast', 'target'];

        document.querySelectorAll('[data-action]:not([data-action="favorite"]):not([data-action="open-modal"])')
            .forEach(function (btn) {
                btn.addEventListener('click', function () {
                    var url = btn.dataset.url;
                    if (!url) {
                        return;
                    }
                    var params = {};
                    Object.keys(btn.dataset).forEach(function (key) {
                        if (RESERVED.indexOf(key) === -1) {
                            params[key] = btn.dataset[key];
                        }
                    });

                    var run = function () {
                        btn.disabled = true;
                        Minsu.post(url, params).then(function (result) {
                            Minsu.toast(result.msg || '操作成功', 'success');
                            if (btn.dataset.reload !== 'false') {
                                setTimeout(function () {
                                    window.location.reload();
                                }, 800);
                            } else {
                                btn.disabled = false;
                            }
                        }).catch(function () {
                            btn.disabled = false;
                        });
                    };

                    if (btn.dataset.confirm) {
                        Minsu.confirm(btn.dataset.confirm, run, btn.dataset.confirmText || '确定');
                    } else {
                        run();
                    }
                });
            });
    }

    /* ==================== 模态框 ==================== */

    function openModal(id) {
        var modal = document.getElementById(id);
        if (modal) {
            modal.classList.add('show');
        }
    }

    function closeModal(id) {
        var modal = document.getElementById(id);
        if (modal) {
            modal.classList.remove('show');
        }
    }

    function bindModals() {
        document.querySelectorAll('[data-action="open-modal"]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var modalId = btn.dataset.target;
                var modal = document.getElementById(modalId);
                if (!modal) {
                    return;
                }
                // 把按钮上的 data-field-* 值填入模态框内的表单
                Object.keys(btn.dataset).forEach(function (key) {
                    if (key.indexOf('field') === 0) {
                        var fieldName = key.charAt(5).toLowerCase() + key.slice(6);
                        var input = modal.querySelector('[name="' + fieldName + '"]');
                        if (input) {
                            input.value = btn.dataset[key];
                        }
                    }
                });
                openModal(modalId);
            });
        });

        document.querySelectorAll('.modal-close, [data-action="close-modal"]').forEach(function (el) {
            el.addEventListener('click', function () {
                var modal = el.closest('.modal-mask');
                if (modal) {
                    modal.classList.remove('show');
                }
            });
        });

        document.querySelectorAll('.modal-mask').forEach(function (mask) {
            mask.addEventListener('click', function (e) {
                if (e.target === mask) {
                    mask.classList.remove('show');
                }
            });
        });
    }

    /* ==================== 图片上传 ==================== */

    /**
     * 初始化房源图片上传区。
     * 支持点击选择与拖拽上传，上传成功后把图片地址追加到文本域中。
     */
    function initUpload() {
        var zone = document.getElementById('uploadZone');
        var input = document.getElementById('uploadInput');
        var preview = document.getElementById('imagePreview');
        var textarea = document.getElementById('imageUrls');
        if (!zone || !input || !textarea) {
            return;
        }

        zone.addEventListener('click', function () {
            input.click();
        });

        ['dragenter', 'dragover'].forEach(function (event) {
            zone.addEventListener(event, function (e) {
                e.preventDefault();
                zone.classList.add('dragover');
            });
        });
        ['dragleave', 'drop'].forEach(function (event) {
            zone.addEventListener(event, function (e) {
                e.preventDefault();
                zone.classList.remove('dragover');
            });
        });
        zone.addEventListener('drop', function (e) {
            if (e.dataTransfer.files.length) {
                uploadFiles(e.dataTransfer.files);
            }
        });
        input.addEventListener('change', function () {
            uploadFiles(input.files);
        });

        /** 逐个上传选中的文件 */
        function uploadFiles(files) {
            Array.prototype.forEach.call(files, function (file) {
                if (!file.type.startsWith('image/')) {
                    Minsu.toast('仅支持上传图片文件', 'error');
                    return;
                }
                if (file.size > 5 * 1024 * 1024) {
                    Minsu.toast('图片大小不能超过 5MB', 'error');
                    return;
                }
                var formData = new FormData();
                formData.append('file', file);

                fetch('/admin/upload/image', {
                    method: 'POST',
                    headers: {'X-Requested-With': 'XMLHttpRequest'},
                    body: formData
                }).then(function (response) {
                    return response.json();
                }).then(function (result) {
                    if (result.code !== 200) {
                        Minsu.toast(result.msg || '上传失败', 'error');
                        return;
                    }
                    appendImage(result.data.url);
                    Minsu.toast('图片上传成功', 'success');
                }).catch(function () {
                    Minsu.toast('上传失败，请重试', 'error');
                });
            });
            input.value = '';
        }

        /** 把新上传的图片地址写入文本域并渲染缩略图 */
        function appendImage(url) {
            var current = textarea.value.trim();
            textarea.value = current ? current + '\n' + url : url;
            renderPreview();
        }

        /** 依据文本域内容重新渲染预览区 */
        function renderPreview() {
            if (!preview) {
                return;
            }
            preview.innerHTML = '';
            textarea.value.split('\n').forEach(function (line, index) {
                var url = line.trim();
                if (!url) {
                    return;
                }
                var item = document.createElement('div');
                item.className = 'image-preview-item';
                item.innerHTML = '<img src="' + url + '" alt="房源图片">' +
                    '<span class="remove" title="移除">&times;</span>';
                item.querySelector('.remove').onclick = function () {
                    var lines = textarea.value.split('\n').filter(function (l) {
                        return l.trim() !== url;
                    });
                    textarea.value = lines.join('\n');
                    renderPreview();
                };
                preview.appendChild(item);
            });
        }

        // 页面载入时先渲染已有图片
        renderPreview();
        textarea.addEventListener('blur', renderPreview);
    }

    /* ==================== 图表 ==================== */

    /**
     * 通用柱状图 / 折线图 / 饼图渲染。
     * 使用 CDN 引入的 Chart.js，统一配色与样式，避免各图表风格不一。
     */
    var PALETTE = ['#0f766e', '#14b8a6', '#f59e0b', '#3b82f6', '#8b5cf6',
        '#ec4899', '#10b981', '#f97316', '#6366f1', '#84cc16'];

    function renderBar(canvasId, chartData, horizontal) {
        var canvas = document.getElementById(canvasId);
        if (!canvas || !window.Chart) {
            return;
        }
        new Chart(canvas, {
            type: 'bar',
            data: {
                labels: chartData.labels,
                datasets: [{
                    label: chartData.name,
                    data: chartData.values,
                    backgroundColor: '#14b8a6',
                    borderRadius: 5,
                    maxBarThickness: 38
                }]
            },
            options: {
                indexAxis: horizontal ? 'y' : 'x',
                responsive: true,
                maintainAspectRatio: false,
                // 留出左侧内边距，避免纵轴刻度标签被画布边缘裁切
                layout: {padding: {left: 10, right: 12, top: 6}},
                plugins: {legend: {display: false}},
                scales: {
                    y: {beginAtZero: true, grid: {color: '#f1f5f9'}},
                    x: {grid: {display: false}}
                }
            }
        });
    }

    function renderLine(canvasId, chartData) {
        var canvas = document.getElementById(canvasId);
        if (!canvas || !window.Chart) {
            return;
        }
        var ctx = canvas.getContext('2d');
        // 折线下方填充渐变色，视觉上更有层次
        var gradient = ctx.createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(20, 184, 166, .32)');
        gradient.addColorStop(1, 'rgba(20, 184, 166, .02)');

        new Chart(canvas, {
            type: 'line',
            data: {
                labels: chartData.labels,
                datasets: [{
                    label: chartData.name,
                    data: chartData.values,
                    borderColor: '#0f766e',
                    backgroundColor: gradient,
                    fill: true,
                    tension: .35,
                    pointBackgroundColor: '#0f766e',
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    borderWidth: 2.5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                layout: {padding: {left: 10, right: 12, top: 6}},
                plugins: {legend: {display: false}},
                scales: {
                    y: {beginAtZero: true, grid: {color: '#f1f5f9'}},
                    x: {grid: {display: false}}
                }
            }
        });
    }

    function renderDoughnut(canvasId, chartData) {
        var canvas = document.getElementById(canvasId);
        if (!canvas || !window.Chart) {
            return;
        }
        new Chart(canvas, {
            type: 'doughnut',
            data: {
                labels: chartData.labels,
                datasets: [{
                    data: chartData.values,
                    backgroundColor: PALETTE,
                    borderWidth: 3,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '58%',
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {padding: 14, usePointStyle: true, pointStyle: 'circle'}
                    }
                }
            }
        });
    }

    /* ==================== 侧边栏（移动端） ==================== */

    function initSidebarToggle() {
        var toggle = document.getElementById('sidebarToggle');
        var sidebar = document.querySelector('.admin-sidebar');
        if (toggle && sidebar) {
            toggle.addEventListener('click', function () {
                sidebar.classList.toggle('open');
            });
        }
    }

    /* ==================== 页面初始化 ==================== */

    document.addEventListener('DOMContentLoaded', function () {
        bindActionButtons();
        bindModals();
        initUpload();
        initSidebarToggle();
    });

    window.Admin = {
        openModal: openModal,
        closeModal: closeModal,
        renderBar: renderBar,
        renderLine: renderLine,
        renderDoughnut: renderDoughnut
    };

})(window, document);
