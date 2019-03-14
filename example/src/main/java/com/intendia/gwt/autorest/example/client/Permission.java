package com.intendia.gwt.autorest.example.client;

import com.google.common.base.Strings;

public enum Permission {

    /** It allow the user start new Reservation, show new booking button */
    START_NEW_BOOKING("start.new.booking"),

    /** It allow the user start new agency booking, show corresponding elements */
    START_AGENCY_NEW_BOOKING("start.agency.new.booking"),

    /** It allow the logged user find, view bookings and use booking search view */
    SEARCH_AND_VIEW_BOOKINGS("search.and.view.bookings"),

    /** It allows the user to store edited booking. There is a corner case: when Permission.SHOW_PAY_NOW
     *  is granted, but PayNow is not available because of UiCommon.isPayNowAvailable(resStatus, amountDue, XDaysBeforeDeparture),
     *  then Store button will be available despite of this permission.
     */
    STORE_BOOKING("store.booking"),

    /** It allow the user set reservation contact */
    SET_RESERVATION_CONTACT("set.reservation.contact"),

    /** It allow the logged-in customer/client to be substituted as first guest on the new booking automatically */
    SET_SESSION_CLIENT_AS_FIRST_GUEST("set.session.client.as.first.guest"),

    /** Display "Payment Link" button */
    SHOW_PAYMENT_LINK("show.payment.link"),

    /** Enable "Payment Link" button */
    SEND_PAYMENT_LINK("send.payment.link"),

    /** It allow the user skip guest edit stage in booking process */
    CAN_BE_SKIP_GUEST_EDIT_STAGE("can.be.skip.guest.edit.stage"),

    /** It allow the user skip agency search stage in agency new booking */
    CAN_BE_SKIP_AGENCY_STAGE("can.be.skip.agency.stage"),

    /** It allow the user use guestInfo view */
    DISPLAY_GUESTS_INFO("display.guests.info"),

    /** It allow the user use guestEdit view */
    DISPLAY_GUESTS_EDIT("display.guests.edit"),

    /** It allow the user see and manage Client Information, Householder clients.
     * This permission is possible deprecated and overly broad.
     * For example, we can add separate permissions to manage households, view certain client information, etc.
     * */
    MANAGE_CLIENT_INFO("manage.client.info"),

    /** It allow the user see and manage Client Coupons */
    MANAGE_CLIENT_COUPONS("manage.client.coupons"),

    /** It allow the user change client phone and email */
    MANAGE_EXIST_CLIENT_PHONE_AND_EMAIL("manage.exist.client.phone.and.email"),

    /** It allow the user change client last name */
    MANAGE_EXIST_CLIENT_LAST_NAME("manage.exist.client.last.name"),

    /** It allow the user search exist client by club account, and manage club account data */
    MANAGE_CLIENT_CLUB_ACCOUNT("manage.client.club.account"),

    /** It allow the user select available market and currency on Guest Info View */
    MANAGE_MARKET_AND_CURRENCY("manage.market.and.currency"),

    /** It allow the user assign guest type like a CREW, EMPLOYER, JUNKET, VENDORS e.t.c. */
    ASSIGN_GUEST_TYPE("assign.guest.type"),

    /** It allow the user manage promotions, show promotions controls and fields */
    MANAGE_PROMOTIONS("manage.promotions"),

    /** It allow the user manage allotment, show allotment control */
    MANAGE_ALLOTMENT("manage.allotment"),

    /** It allow the user choose cruise theme */
    CHOOSE_CRUISE_THEME("choose.cruise.theme"),

    /** It allow the user choose available cabin price */
    CHOOSE_STATEROOM_PRICE("choose.stateroom.price"),

    /** It allow the user see cabin category code */
    SHOW_CABIN_CATEGORY_CODE("show.cabin.category.code"),

    /** It allow the user see cabin book limit, see also setting cabin.can.book.limit */
    SHOW_CABIN_BOOK_LIMIT("show.cabin.book.limit"),

    /** It allow the user select certain stateroom in ship */
    ASSIGN_CABIN_NUMBER("assign.cabin.number"),

    /** It not allow the user do voyage search without specifing his residence */
    RESIDENCY_IS_MANDATORY("residency.mandatory"),

    /** It allow the user use additional shore excursion filters */
    MORE_SHOREX_FILTERS("more.shorex.filters"),

    /** It allow the user manage agents in agency search results */
    MANAGE_AGENTS("manage.agents"),

    /** It allows the user to add new agent */
    ADD_NEW_AGENT("add.new.agent"),

    /** It allow the user manage "agent access all data" when editing agent */
    MANAGE_AGENT_ACCESS_ALL_DATA("manage.agent.access.all.data"),

    /** It allow to set guests amount search parameter when search cruise */
    MANAGE_GUESTS_NUM_SEARCH_PARAM("manage.guests.num.searh.param"),

    /** It allow the user view, print and send by email agency confirmation docs */
    MANAGE_AGENCY_CONFIRMATION_DOCS("manage.agency.confirmation.docs"),

    /** It allow the user view, print and send by email agent confirmation docs */
    MANAGE_AGENT_CONFIRMATION_DOCS("manage.agent.confirmation.docs"),

    /** It allow the user assign agent-ref for booking. */
    ASSIGN_BOOKING_AGENT_REF("assign.booking.agent.ref"),

    /** Show "departing from" field on "Cruises" view. */
    SHOW_DEPARTING_FROM_PARAM("show.departing.from.param"),

    /** It allow the user see more itinerary params like a Pier, Embark, Disembark e.t.c. */
    SHOW_MORE_ITINERARY_PARAMS("show.more.itinerary.params"),

    /** It allow the user see agent commission in invoice details */
    SHOW_AGENT_COMMISSION_INVOICE("show.agent.comission.invoice"),

    /** It allow showing of agency info */
    SHOW_AGENCY_INFO("show.agency.info"),

    /** It allow to search and change agency */
    MANAGE_AGENCY("manage.agency"),

    /** It allow the user specify and store group */
    MANAGE_GROUP("manage.group"),

    /** It allow the user see booking source */
    SHOW_BOOKING_SOURCE("show.booking.source"),

    /** It allow to filter ships by brands */
    APPLY_SHIP_DEFAULT_BRANDS("apply.ship.default.brands"),

    /** It allow the user show pay now */
    SHOW_PAY_NOW("show.pay.now"),

    /** It allow the user search bookings by ship */
    SEARCH_BOOKINGS_BY_SHIP("search.bookings.by.ship"),

    /** It allow the user search bookings by agency and agent */
    SEARCH_BOOKINGS_BY_AGENCY_AND_AGENT("search.bookings.by.agency.and.agent"),

    /** It allows the user to search voyages and filter staterooms by reserved type */
    MANAGE_RESERVE_TYPE_FILTER("manage.reserve.type.filter"),

    /** It allows the user filter staterooms by cabin attributes */
    MANAGE_CABIN_ATTR_FILTER("manage.cabin.attr.filter"),

    /** It allow the user manage Client Preferences */
    MANAGE_CLENT_PREFERENCES("manage.client.preferences"),

    /** It allow the user see promotions with exclusion non selectable classification promo types */
    APPLY_NON_SELECT_PROMOTIONS("apply.non.select.promotions"),

    /** It allow the user set reservation name */
    MANAGE_BOOKING_NAME("manage.booking.name"),

    /** It allow the user set reservation type */
    MANAGE_BOOKING_TYPE("manage.booking.type"),

    /** It allow the user to see highlight allotment */
    HIGHLIGHT_ALLOTMENT("highlight.allotment"),

    /** It allow the user to cancel bookings */
    MANAGE_BOOKING_CANCELLATION("manage.booking.cancellation"),

    /** It allow the user add clients to editing booking */
    ADD_CLIENTS_TO_EXIST_BOOKING("add.clients.to.exist.booking"),

    /** It allow the user delete clients from editing booking */
    DELETE_CLIENTS_FROM_EXIST_BOOKING("delete.clients.from.exist.booking"),

    /** It allow the user change cabin count */
    EDIT_CABIN_COUNT_IN_EXIST_BOOKING("edit.cabin.count.in.exist.booking"),

    /** It allow the user search and view bookings on ResSummary page */
    SHOW_RESSUMMARY_TEST_STAGE("show.ressummary.test.stage"),

    /** It allow the user search cruises */
    CAN_SEARCH_CRUISES("can.search.cruises"),

    /** It allow the user edit exist Bookings */
    EDIT_EXIST_BOOKINGS("edit.exist.bookings"),

    /** It allow the user add/delete comments for booking, without permission read only */
    MANAGE_BOOKING_COMMENTS("manage.booking.comments"),

    /** It allow the user view/print booking confirmation docs */
    MANAGE_BOOKING_DOCS("manage.booking.docs"),

    /** It allow the user edit exist clients data */
    EDIT_EXIST_CLIENTS_DATA("edit.exist.clients.data"),

    /** It allow the user generate jasper reports */
    MANAGE_JASPER_REPORTS("manage.jasper.reports"),

    /** It allow the user use staterooms view */
    DISPLAY_STATEROOMS("display.staterooms"),

    /** It allow the user use ressummary view*/
    DISPLAY_RES_SUMMARY("display.res.summary"),

    /** It allow the user assign addons for booking */
    MANAGE_ADDONS("manage.addons"),

    /** It allow the user assign shore excursions for booking */
    MANAGE_SHOREXES("manage.shorexes"),

    /** It allow the user assign special requests for booking */
    MANAGE_SPECIAL_REQUESTS("manage.special.requests"),

    /** It allow the user assign land programs for booking */
    MANAGE_LAND_PROGRAMS("manage.land.programs"),

    /** It allow the user change addons for exist booking */
    EDIT_ADDONS_IN_EXIST_BOOKING("edit.addons.in.exist.booking"),

    /** It allow the user change shore excursions for exist booking */
    EDIT_SHOREXES_IN_EXIST_BOOKING("edit.shorexes.in.exist.booking"),

    /** It allow the user change special requests for exist booking */
    EDIT_SPECIAL_REQUESTS_IN_EXIST_BOOKING("edit.special.requests.in.exist.booking"),

    /** It allow the user change land programs for exist booking */
    EDIT_LAND_PROGRAMS_IN_EXIST_BOOKING("edit.land.programs.in.exist.booking"),

    /** It allow the agent manage web security groups */
    MANAGE_WEB_SECURITY_GROUPS("manage.web.security.groups"),

    /** It allow the user start new Group */
    START_NEW_GROUP("start.new.group"),

    /** It allow the user manage extension bookings */
    MANAGE_EXT_BOOKINGS("manage.extension.bookings"),

    /** It allow the user manage payment extension */
    MANAGE_PAYMENT_EXTENSION("manage.payment.extension"),

    /** It allow the user manage air ribbon section */
    MANAGE_AIR_RIBBON("manage.air.ribbon"),

    /** Display only promotion name without code */
    SHOW_PROMOTION_NAME_ONLY("show.promotion.name.only"),

    /** It allow the user set middle name for guest */
    MANAGE_GUEST_MIDDLE_NAME("manage.guest.middle.name"),

    /** It allow the user to manage by transfer transactions */
    MANAGE_TRANSFER_TRANSACTIONS("manage.transfer.transactions"),

    /** It allow the user set reservation referral code */
    MANAGE_REFERRAL_SOURCE("manage.referral.source"),

    /** It allow the user manage air section: booked and independent */
    MANAGE_AIR_SECTION("manage.air.section"),

    /** It allow the user edit exist Group */
    EDIT_EXIST_GROUP("edit.exist.group"),

    /** It allow the user search bookings by air */
    SEARCH_BOOKINGS_BY_AIR("search.bookings.by.air"),

    /** It allow the user manage travel with functionality */
    ALLOW_TRAVEL_WITH("allow.travel.with"),

    /** It allow the user search bookings by hotel */
    SEARCH_BOOKINGS_BY_HOTEL("search.bookings.by.hotel"),

    /** It allow the user search bookings by dining */
    SEARCH_BOOKINGS_BY_DINING("search.bookings.by.dining"),

    /** It allow the user reinstate canceled bookings */
    ALLOW_REINSTATE_BOOKING("allow.reinstate.booking"),

    /** Allows showing of invoice details grid */
    SHOW_INVOICE_DETAILS("show.invoice.details"),

    /** Allows showing of reservation history */
    SHOW_BOOKING_HISTORY("show.booking.history"),

    /** It allow the user manage invoice manual adjustment */
    MANAGE_INVOICE_MANUAL_ADJUSTMENT("manage.invoice.manual.adjustment"),

    /** It allow the user set birthday for guest on guest initial page*/
    MANAGE_BIRTHDAY_AT_INITIAL_GUEST("manage.birthday.at.initial.guest"),

    /** It allow the user to upgrade cabin */
    ALLOW_CABIN_UPGRADE("allow.cabin.upgrade"),

    /** It allows the agent to add the cabins in booking */
    ADD_CABINS_IN_BOOKING("add.cabins.in.booking"),

    /** It allows the agent to attach the booking to group */
    ALLOW_ATTACH_BOOKING_TO_GROUP("allow.attach.booking.to.group"),

    /** It allows the agent to detach the booking from group */
    ALLOW_DETACH_BOOKING_FROM_GROUP("allow.detach.booking.from.group"),

    /** It allow the user search bookings by user who created reservation */
    SEARCH_BOOKINGS_BY_CREATOR("search.bookings.by.creator"),

    /** It allow the logged user find, view client bookings using agency link */
    SEARCH_AND_VIEW_CLIENT_BOOKINGS("search.and.view.client.bookings"),

    /** It allow the logged user find bookings, select and create invoice */
    SEARCH_AND_CREATE_INVOICE("search.and.create.invoice"),

    /** It allow the user show pay now for group */
    SHOW_GROUP_PAY_NOW("show.group.pay.now"),

    /** It allows the user to select multiple bookings from search results */
    SELECT_MULTIPLE_BOOKINGS("select.multiple.bookings"),

    /** It allows the user to view/add addon notes */
    MANAGE_ADDON_NOTES("manage.addon.notes"),

    /** It allows the user to void transactions in pending status */
    VOID_PENDING_PAYMENT_TRANSACTIONS("void.pending.payment.transactions"),

    /** It allow the user show used/redeemed points */
    SHOW_USED_POINTS("show.used.points"),

    /** It allow the user assign ship requests for group */
    MANAGE_SHIP_REQUESTS("manage.ship.requests"),

    /** It allow the user import and export guests to xml file */
    IMPORT_AND_EXPORT_GUESTS("import.export.guests"),

    /** It allow the user view, print and send by email consumer confirmation docs */
    MANAGE_CONSUMER_CONFIRMATION_DOCS("manage.consumer.confirmation.docs"),

    /** It allow the user reinstate canceled group */
    ALLOW_REINSTATE_GROUP("allow.reinstate.group"),

    /** It allow the user change language of confirmation docs */
    EDIT_LANGUAGE_CONFIRMATION_DOCS("edit.language.confirmation.docs"),

    /** It allow the user manage/show shared cabin control */
    MANAGE_SHARED_CABIN("manage.shared.cabin"),

    /** It allow the user see linked account popup */
    SHOW_LINKED_ACCOUNT_POPUP("show.linked.account.popup"),

    /**It allows the user to manage company id in header */
    ALLOW_HEADER_EDIT_COMPANY("allow.header.edit.company"),

    /** It allow the user see Visa-free Rule row on Terms and Conditions stage*/
    SHOW_PAYMENT_CHECKBOX_VISA_FREE("show.payment.checkbox.visa.free"),

    /** It allow the user to refresh booking information on summary page*/
    ALLOW_REFRESH_BOOKING("allow.refresh.booking"),

    /** It allow the user to create and quote "fast" booking on the only one page */
    MANAGE_QUICK_BOOKING("manage.quick.booking"),

    /** It allow the user to assign cabins to WTL */
    ASSIGN_WTL_CABIN("assign.wtl.cabin"),

    /** It allow the user see reservation type and name */
    SHOW_BOOKING_TYPE_AND_NAME("show.booking.type.and.name"),

    /** It allow the user see external documents from cms at Main Menu More option */
    SHOW_CMS_EXTERNAL_DOCUMENTS("show.cms.external.documents"),

    /** It allow the user manage component payment */
    MANAGE_COMPONENT_PAYMENT("manage.component.payment"),

    /** It allows the user to apply gift cards */
    ALLOW_GIFT_CARD_PAYMENT("allow.gift.card.payment"),

    /** It allows the user to manage gift card payment */
    MANAGE_GIFT_CARD_PAYMENT("manage.gift.card.payment"),

    /** It allows the user to use advanced mode of point redemption wizard */
    ALLOW_ADVANCED_POINT_REDEMPTION("allow.advanced.point.redemption"),

    /** It allows the user to store booking in WAITLIST status */
    STORE_BOOKING_IN_WL_STATUS("store.booking.in.wl.status"),

    /** It allows the user to change WL booking status */
    SWITCH_WL_BOOKING_STATUS("switch.wl.booking.status"),

    /** It allows the user to edit WL bookings */
    EDIT_WL_BOOKINGS("edit.wl.bookings"),

    /** It allows the user to show extra info on voyage search */
    SHOW_VOYAGE_SEARCH_EXTRA_INFO("show.voyage.search.extra.info"),

    /** It allows the user to manage payment tokens */
    MANAGE_PAYMENT_TOKENS("manage.payment.tokens"),

    /** It allows the user to delete payment tokens */
    DELETE_PAYMENT_TOKENS("delete.payment.tokens"),

    /** It allows the user to show news page*/
    ALLOW_SHOW_NEWS_PAGE("allow.show.news.page"),

    /** It allows the user to use back to back functionality */
    ALLOW_BACK2BACK("allow.back2back"),

    /** It allows the user to manage custom segment */
    MANAGE_CUSTOM_SEGMENT("manage.custom.segment"),

    /** It allow the user to manage Invoices Per Guest flag */
    MANAGE_CHECKBOX_INVOICES_PER_GUEST("manage.checkbox.invoices.per.guest");

    private final String stringValue;

    Permission(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }

    public static Permission fromString(String stringValue) {
        Permission permission = null;

        if (!Strings.isNullOrEmpty(stringValue)) {
            for (Permission p : Permission.values()) {
                if (stringValue.equals(p.stringValue)) {
                    permission = p;
                    break;
                }
            }
        }
        return permission;
    }
}
