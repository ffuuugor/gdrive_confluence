package ffuuugor.atlassian.gdrive;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.page.*;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import javax.inject.Inject;

import java.io.Writer;
import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.user.User;

@Scanned
public class GDriveImportServlet extends HttpServlet
{
    private static final Logger log = LoggerFactory.getLogger(GDriveImportServlet.class);
    @ComponentImport
    private final UserAccessor userAccessor;
    @ComponentImport
    private final SpaceManager spaceManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final PageService pageService;

    @Inject
    public GDriveImportServlet(UserAccessor userAccessor,
                               LoginUriProvider loginUriProvider,
                               TemplateRenderer templateRenderer,
                               PageService pageService,
                               SpaceManager spaceManager)
    {
        this.userAccessor = userAccessor;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.spaceManager = spaceManager;
        this.pageService = pageService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getRemoteUser();
        if (username == null) {
            redirectToLogin(request, response);
            return;
        }

        templateRenderer.render("page.vm", response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        String username = req.getRemoteUser();
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        log.debug("Creating new page " + title + " on behalf of user " + username);

        Page created = tryCreatePage(username, title, content);
        Writer w = response.getWriter();
        w.write(String.valueOf(created.getId()));
        w.close();

        log.debug("Successfully created page " + created.getId());
    }

    private Page tryCreatePage(String username, final String title, final String content) {
        User user = userAccessor.getUserByName(username);
        final Space space = spaceManager.getAllSpaces().get(1);
        final Page spaceRoot = space.getHomePage();

        PageProvider pageProvider = new PageProvider() {
            public Page getPage() {
                Page page = new Page();
                page.setSpace(space);
                page.setParentPage(spaceRoot);
                page.setBodyAsString(content.replace("\n","<br/>"));
                page.setTitle(title);
                spaceRoot.addChild(page);
                return page;
            }
        };
        SimpleContentPermissionProvider permissions = new SimpleContentPermissionProvider();
        CreatePageCommand serviceCommand = (CreatePageCommand) pageService.newCreatePageCommand(pageProvider,
                permissions, user, false);
        serviceCommand.execute();

        return serviceCommand.getCreatedPage();
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}