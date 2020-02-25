/**
 * Copyright (c) 2012--2020 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.domain.action.scap;

import com.redhat.rhn.common.db.datasource.DataResult;
import com.redhat.rhn.common.db.datasource.ModeFactory;
import com.redhat.rhn.common.db.datasource.SelectMode;
import com.redhat.rhn.common.localization.LocalizationService;
import com.redhat.rhn.domain.action.Action;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.domain.user.User;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * ScapAction - Class representing TYPE_SCAP_*.
 * @version $Rev$
 */
public class ScapAction extends Action {

    private ScapActionDetails scapActionDetails;

    /**
     * @return Returns the scapActionDetails.
     */
    public ScapActionDetails getScapActionDetails() {
        return scapActionDetails;
    }

    /**
     * @param scapActionDetailsIn The scapActionDetails to set.
     */
    public void setScapActionDetails(ScapActionDetails scapActionDetailsIn) {
        scapActionDetailsIn.setParentAction(this);
        scapActionDetails = scapActionDetailsIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHistoryDetails(Server server, User currentUser) {
        LocalizationService ls = LocalizationService.getInstance();
        StringBuilder retval = new StringBuilder();
        retval.append("</br>");
        retval.append(ls.getMessage("system.event.scapPath"));
        retval.append(StringEscapeUtils.escapeHtml(scapActionDetails.getPath()));
        retval.append("</br>");
        retval.append(ls.getMessage("system.event.scapParams"));
        retval.append(scapActionDetails.getParameters() == null ? "" :
            StringEscapeUtils.escapeHtml(scapActionDetails.getParametersContents()));
        if (this.getSuccessfulCount() > 0) {
            SelectMode m = ModeFactory.getMode("scap_queries", "lookup_xccdftestresult_id");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("action_scap_id", scapActionDetails.getId());
            params.put("server_id", server.getId());
            DataResult<Map<String, Long>> dr = m.execute(params);
            Long xccdfDetailId = dr.get(0).get("id");
            retval.append("</br>");
            retval.append("<a href=\"/rhn/systems/details/audit/XccdfDetails.do?sid=" +
                    server.getId() + "&xid=" + xccdfDetailId + "\">");
            retval.append(ls.getMessage("system.event.scapDownload"));
            retval.append("</a>");
        }
        return retval.toString();
    }

}
