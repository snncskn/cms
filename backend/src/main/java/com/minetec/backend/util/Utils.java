package com.minetec.backend.util;

import com.minetec.backend.entity.AbstractEntity;
import io.micrometer.core.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sinan
 */
public final class Utils extends org.springframework.util.StringUtils {

    private static final AtomicInteger HOUR = new AtomicInteger(24);
    public static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH);

    /**
     * scale 4, HALF_DOWN
     *
     * @param value
     * @return
     */
    public static BigDecimal toBigDecimal(final String value) {
        if (value != null) {
            return new BigDecimal(value).setScale(AbstractEntity.CURRENCY_SCALE, RoundingMode.HALF_DOWN);
        }
        return BigDecimal.ZERO;
    }

    /**
     *
     * @param value
     * @return
     */
    public static Integer toInteger(final String value) {
        if (value != null) {
            return Integer.parseInt(value);
        }
        return 0;
    }


    /**
     * @param value
     * @return
     */
    public static Long toLong(final String value) {
        if (value != null) {
            return Long.parseLong(value);
        }
        return 0L;
    }

    /**
     * pattern : yyyy-MM-dd HH:mm
     *
     * @param value
     * @return
     */
    public static LocalDateTime toLocalDate(final String value) {
        if (!isEmpty(value)) {
            return LocalDateTime.parse(value, pattern);
        }
        return null;
    }

    /**
     * @param collection
     * @return
     */
    public static boolean isEmpty(@Nullable final Collection<?> collection) {
        return org.springframework.util.CollectionUtils.isEmpty(collection);
    }

    /**
     * @param map
     * @return
     */
    public static boolean isEmpty(@Nullable final Map<?, ?> map) {
        return org.springframework.util.CollectionUtils.isEmpty(map);
    }


    /**
     * pattern : yyyy-MM-dd HH:mm
     *
     * @param value
     * @return
     */
    public static String toString(final LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(pattern);
    }

    /**
     * @param firstDecimal
     * @param secondDecimal
     * @return
     */
    public static boolean equals(@NotNull final BigDecimal firstDecimal, @NotNull final BigDecimal secondDecimal) {
        var first = firstDecimal.setScale(AbstractEntity.CURRENCY_SCALE);
        var second = secondDecimal.setScale(AbstractEntity.CURRENCY_SCALE);
        return first.equals(second);
    }


    /**
     * @param firstDate
     * @param stopDate
     * @return
     */
    public static String formatterDaysAndHour(@NotNull final LocalDateTime firstDate,
                                              @NotNull final LocalDateTime stopDate) {
        if (isEmpty(firstDate) || isEmpty(stopDate)) {
            return null;
        }
        long diffDays = ChronoUnit.DAYS.between(firstDate, stopDate);
        long diffHours = ChronoUnit.HOURS.between(firstDate, stopDate);

        diffHours = diffHours > HOUR.get() ? diffHours % HOUR.get() : diffHours;

        return new StringBuilder(" ").append(diffDays).append(" Days ")
            .append((diffHours)).append(" Hours.").toString();
    }

    /**
     * @param firstDate
     * @param stopDate
     * @return
     */
    public static long calcDifferenceHours(@NotNull final LocalDateTime firstDate,
                                             @NotNull final LocalDateTime stopDate) {
        if (isEmpty(firstDate) || isEmpty(stopDate)) {
            return 0L;
        }
        long diffHours = ChronoUnit.HOURS.between(firstDate, stopDate);
        return diffHours;
    }


    /**
     * @param value
     * @return
     */
    public static String toString(@NotNull final BigDecimal value) {
        if (value == null) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * @param value
     * @return
     */
    public static String toString(@NotNull final Integer value) {
        if (value == null) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }
}
