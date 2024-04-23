package com.ctrip.car.osd.framework.common.utils;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期范围
 * 
 * @author xiayx@ctrip.com
 *
 */
public enum DateRangeUnit {
	
	seconds {
		@Override
		protected Date begin(Date date) {
			date = DateUtils.setMilliseconds(date, 0);
			return date;
		}

		@Override
		protected Date end(Date date) {
			date = DateUtils.setMilliseconds(date, 999);
			return date;
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addSeconds(date, unit);
			return date;
		}
	},
	
	minus{
		@Override
		protected Date begin(Date date) {
			date = DateUtils.setSeconds(date, 0);
			date = DateUtils.setMilliseconds(date, 0);
			return date;
		}

		@Override
		protected Date end(Date date) {
			date = DateUtils.setSeconds(date, 59);
			date = DateUtils.setMilliseconds(date, 999);
			return date;
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addMinutes(date, unit);
			return date;
		}
	},
	hour{
		
		@Override
		protected Date begin(Date date) {
			date = DateUtils.setMinutes(date, 0);
			date = DateUtils.setSeconds(date, 0);
			date = DateUtils.setMilliseconds(date, 0);
			return date;
		}

		@Override
		protected Date end(Date date) {
			date = DateUtils.setMinutes(date, 59);
			date = DateUtils.setSeconds(date, 59);
			date = DateUtils.setMilliseconds(date, 999);
			return date;
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addHours(date, unit);
			return date;
		}
		
	},
	day {
		@Override
		protected Date begin(Date date) {
			return min(date);
		}

		@Override
		protected Date end(Date date) {
			return max(date);
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addDays(date, unit);
			return date;
		}

		@Override
		public boolean isDay() {
			return true;
		}
	},
	week {
		@Override
		protected Date begin(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK) - (calendar.getFirstDayOfWeek() + 1);
			date = DateUtils.addDays(date, -weekOfDay);
			return min(date);
		}

		@Override
		protected Date end(Date date) {
			date = begin(date);
			date = DateUtils.addDays(date, 6);
			return max(date);
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addWeeks(date, unit);
			return date;
		}

		@Override
		public boolean isWeek() {
			return true;
		}
	},
	month {
		@Override
		protected Date begin(Date date) {
			date = DateUtils.setDays(date, 1);
			return min(date);
		}

		@Override
		protected Date end(Date date) {
			date = DateUtils.setDays(date, 1);
			date = DateUtils.addMonths(date, 1);
			date = DateUtils.addDays(date, -1);
			return max(date);
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addMonths(date, unit);
			return date;
		}

		@Override
		public boolean isMonth() {
			return true;
		}
	},
	year {
		@Override
		protected Date begin(Date date) {
			date = DateUtils.setMonths(date, 0);
			date = DateUtils.setDays(date, 1);
			return min(date);
		}

		@Override
		protected Date end(Date date) {
			date = DateUtils.setDays(date, 30);
			date = DateUtils.setMonths(date, 11);
			return max(date);
		}

		@Override
		protected Date getDate(Date date, int unit) {
			date = DateUtils.addYears(date, unit);
			return date;
		}

		@Override
		public boolean isYear() {
			return true;
		}
	},;

	protected Date min(Date date) {
		date = DateUtils.setHours(date, 0);
		date = DateUtils.setMinutes(date, 0);
		date = DateUtils.setSeconds(date, 0);
		date = DateUtils.setMilliseconds(date, 0);
		return date;
	}

	protected Date max(Date date) {
		date = DateUtils.setHours(date, 23);
		date = DateUtils.setMinutes(date, 59);
		date = DateUtils.setSeconds(date, 59);
		date = DateUtils.setMilliseconds(date, 999);
		return date;
	}

	protected Date begin(Date date) {
		return null;
	}

	protected Date end(Date date) {
		return null;
	}

	public boolean isDay() {
		return false;
	}

	public boolean isWeek() {
		return false;
	}

	public boolean isMonth() {
		return false;
	}

	public boolean isYear() {
		return false;
	}

	public DateRange get(Date date) {
		return DateRange.of(begin(date), end(date), this);
	}

	public DateRange get() {
		return get(new Date());
	}

	protected Date getDate(Date begin, int unit) {
		return new Date();
	}

	public DateRange next(Date begin, int unit) {
		Date date = getDate(begin, unit);
		if (date == null) {
			return DateRange.NULL();
		}
		return get(date);
	}

	public DateRange next(int unit) {
		return next(new Date(), unit);
	}

	public DateRange next() {
		return next(new Date(), 1);
	}

	public DateRange prev(Date begin, int unit) {
		return next(begin, -unit);
	}

	public DateRange prev(int unit) {
		return prev(new Date(), unit);
	}

	public DateRange prev() {
		return prev(new Date(), 1);
	}

	public static DateRangeUnit of(String type) {
		try {
			return valueOf(type.toLowerCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Not Found Date Range Unit . ");
		}
	}

	public static class DateRange {

		private Date begin;
		private Date end;
		private DateRangeUnit type;

		public DateRange(Date begin, Date end, DateRangeUnit type) {
			super();
			this.begin = begin;
			this.end = end;
			this.type = type;
		}

		public Date getBegin() {
			return begin;
		}

		public Date getEnd() {
			return end;
		}

		public DateRange next() {
			return next(1);
		}

		public DateRange next(int unit) {
			if (type == null) {
				return NULL();
			}
			return type.next(begin, unit);
		}

		public DateRange prev(int unit) {
			if (type == null) {
				return NULL();
			}
			return type.prev(begin, unit);
		}

		public DateRange prev() {
			return prev(1);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("begin", DateFormatUtils.format(begin, "yyyy-MM-dd HH:mm:ss"))
					.add("end", DateFormatUtils.format(end, "yyyy-MM-dd HH:mm:ss")).add("type", type).toString();
		}

		public static DateRange NULL() {
			return new DateRange(null, null, null);
		}

		public static DateRange of(Date begin, Date end, DateRangeUnit type) {
			return new DateRange(begin, end, type);
		}
	}

}
