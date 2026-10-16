/* ==========================================================================
   房源详情页脚本
   负责：日期选择与联动、实时价格计算、日期占用校验、下单提交
   ========================================================================== */

(function (window, document) {
    'use strict';

    var houseId = null;
    var unitPrice = 0;
    var maxGuest = 2;
    var bookedRanges = [];

    /** 页面初始化 */
    function init() {
        var panel = document.getElementById('bookingPanel');
        if (!panel) {
            return;
        }
        houseId = panel.dataset.houseId;
        unitPrice = parseFloat(panel.dataset.price) || 0;
        maxGuest = parseInt(panel.dataset.maxGuest, 10) || 2;

        var checkIn = document.getElementById('checkInDate');
        var checkOut = document.getElementById('checkOutDate');
        var today = panel.dataset.today;

        // 入住日期不早于今天，退房日期不早于入住日期
        checkIn.min = today;
        checkOut.min = today;

        checkIn.addEventListener('change', function () {
            checkOut.min = checkIn.value;
            if (checkOut.value && checkOut.value <= checkIn.value) {
                // 入住日期后移导致退房日期失效时，自动顺延一天
                var next = new Date(checkIn.value + 'T00:00:00');
                next.setDate(next.getDate() + 1);
                checkOut.value = Minsu.formatDate(next);
            }
            refreshPrice();
        });
        checkOut.addEventListener('change', refreshPrice);

        loadBookedDates();
        bindSubmit();
    }

    /**
     * 加载该房源已被预订的日期区间。
     * 用于在用户选择日期时给出冲突提示。
     */
    function loadBookedDates() {
        Minsu.get('/house/api/booked-dates', {houseId: houseId}, {silent: true})
            .then(function (result) {
                bookedRanges = result.data || [];
            })
            .catch(function () {
                bookedRanges = [];
            });
    }

    /**
     * 实时计算价格。
     * 调用后端接口而不是在前端直接算，保证价格规则（如最长 30 晚限制、
     * 日期冲突判断）与服务端完全一致，避免两边算法不一致导致的下单失败。
     */
    function refreshPrice() {
        var checkIn = document.getElementById('checkInDate').value;
        var checkOut = document.getElementById('checkOutDate').value;
        var nightsEl = document.getElementById('nightsText');
        var totalEl = document.getElementById('totalText');

        if (!checkIn || !checkOut) {
            nightsEl.textContent = '—';
            totalEl.textContent = '¥ 0';
            return;
        }
        if (checkOut <= checkIn) {
            nightsEl.textContent = '—';
            totalEl.textContent = '¥ 0';
            Minsu.toast('退房日期必须晚于入住日期', 'error');
            return;
        }

        Minsu.get('/house/api/price', {
            houseId: houseId,
            checkIn: checkIn,
            checkOut: checkOut
        }, {silent: true}).then(function (result) {
            var data = result.data;
            nightsEl.textContent = data.nights + ' 晚';
            totalEl.textContent = '¥ ' + Number(data.totalAmount).toFixed(2);
            var tip = document.getElementById('availableTip');
            if (!data.available) {
                tip.textContent = '⚠ 所选日期已被预订，请更换日期';
                tip.style.display = 'block';
            } else {
                tip.style.display = 'none';
            }
        }).catch(function (error) {
            nightsEl.textContent = '—';
            totalEl.textContent = '¥ 0';
            Minsu.toast(error.message || '价格计算失败', 'error');
        });
    }

    /** 绑定下单按钮 */
    function bindSubmit() {
        var btn = document.getElementById('bookingSubmit');
        if (!btn) {
            return;
        }
        btn.addEventListener('click', function () {
            var form = document.getElementById('bookingForm');
            if (!Minsu.validateForm(form)) {
                return;
            }
            var checkIn = document.getElementById('checkInDate').value;
            var checkOut = document.getElementById('checkOutDate').value;
            if (!checkIn || !checkOut) {
                Minsu.toast('请选择入住与退房日期', 'error');
                return;
            }
            var guestNum = parseInt(document.getElementById('guestNum').value, 10);
            if (guestNum > maxGuest) {
                Minsu.toast('该房源最多可住 ' + maxGuest + ' 人', 'error');
                return;
            }

            btn.disabled = true;
            btn.textContent = '提交中…';

            Minsu.post('/order/create', {
                houseId: houseId,
                checkInDate: checkIn,
                checkOutDate: checkOut,
                guestNum: guestNum,
                contactName: document.getElementById('contactName').value,
                contactPhone: document.getElementById('contactPhone').value,
                remark: document.getElementById('remark').value
            }).then(function (result) {
                Minsu.toast('下单成功，正在跳转支付…', 'success');
                setTimeout(function () {
                    window.location.href = '/order/success/' + result.data.orderNo;
                }, 900);
            }).catch(function () {
                btn.disabled = false;
                btn.textContent = '立即预订';
            });
        });
    }

    document.addEventListener('DOMContentLoaded', init);

})(window, document);
