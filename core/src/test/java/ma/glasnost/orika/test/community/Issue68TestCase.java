package ma.glasnost.orika.test.community;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.junit.Test;

public class Issue68TestCase {

	@Test
	public void testSimpleReverceCase() {
		ConfigurableMapper mapper = new ConfigurableMapper() {
			@Override
			public void configure(MapperFactory mapperFactory) {
				mapperFactory.classMap(InvoiceItem.class, InvoiceItemVO.class).byDefault().register();
				mapperFactory.classMap(ProjectItem.class, ProjectItemVO.class).byDefault().register();
				mapperFactory.classMap(Project.class, ProjectVO.class).byDefault().register();			
			}

		};

		ProjectVO projectVO = new ProjectVO();
		ProjectItemVO projectItemVO = new ProjectItemVO();
		InvoiceItemVO invoiceitemVO = new InvoiceItemVO();

		projectItemVO.project = projectVO;
		invoiceitemVO.project = projectVO;

		projectVO.getProjectItems().add(projectItemVO);
		projectVO.name = "Great project";
		projectItemVO.getInvoiceItems().add(invoiceitemVO);

		invoiceitemVO.getProjectItems().add(projectItemVO);

		InvoiceItemProxy invoiceItemProxy = BeanFactory
				.createInvoiceItemProxy();
		mapper.map(invoiceitemVO, invoiceItemProxy);


	}

	public static class BeanFactory {
		public static ProjectProxy createProjectProxy() {
			return new Project();
		}

		public static ProjectItemProxy createProjectItemProxy() {
			return new ProjectItem();
		}

		public static InvoiceItemProxy createInvoiceItemProxy() {
			return new InvoiceItem();
		}
	}

	public static interface ProjectProxy {

		void setProjectItems(Set<ProjectItemProxy> projectItems);

		Set<ProjectItemProxy> getProjectItems();

		void setName(String name);

		String getName();

	}

	public static class Project implements ProjectProxy {

		private String name;
		private Set<ProjectItem> projectItems = new HashSet<ProjectItem>();

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public Set<ProjectItemProxy> getProjectItems() {
			return convertSet(projectItems, ProjectItemProxy.class);
		}

		@Override
		public void setProjectItems(Set<ProjectItemProxy> projectItems) {
			this.projectItems = castSet(projectItems, ProjectItem.class);
		}
	}

	public class ProjectItemProxyFactory implements
			ObjectFactory<ProjectItemProxy> {

		/*
		 * @Override public ProjectItemProxy create(Object source,
		 * Type<ProjectItemProxy> destinationType) { ProjectItemProxy personDto
		 * = new ProjectItem(); return personDto; }
		 */

		@Override
		public ProjectItemProxy create(Object source, MappingContext context) {
			ProjectItemProxy personDto = new ProjectItem();
			return personDto;
		}
	}

	public static interface ProjectItemProxy {

		void setInvoiceItems(Set<InvoiceItemProxy> invoiceItems);

		Set<InvoiceItemProxy> getInvoiceItems();

		void setProject(ProjectProxy project);

		ProjectProxy getProject();

		void setName(String name);

		String getName();

	}

	public static class ProjectItem implements ProjectItemProxy {
		private String name;

		private Project project;

		private Set<InvoiceItem> invoiceItems = new HashSet<InvoiceItem>();

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public ProjectProxy getProject() {
			return project;
		}

		@Override
		public void setProject(ProjectProxy project) {
			this.project = (Project) project;
		}

		@Override
		public Set<InvoiceItemProxy> getInvoiceItems() {
			return convertSet(invoiceItems, InvoiceItemProxy.class);
		}

		@Override
		public void setInvoiceItems(Set<InvoiceItemProxy> invoiceItems) {
			this.invoiceItems = castSet(invoiceItems, InvoiceItem.class);
		}
	}

	public static interface InvoiceItemProxy {

		ma.glasnost.orika.test.community.Issue68TestCase.ProjectProxy getProject();

		void setProjectItems(Set<ProjectItemProxy> projectItems);

		Set<ProjectItemProxy> getProjectItems();

		void setProject(
				ma.glasnost.orika.test.community.Issue68TestCase.ProjectProxy project);

	}

	public static class InvoiceItem implements InvoiceItemProxy {

		private Project project;

		private Set<ProjectItem> projectItems = new HashSet<Issue68TestCase.ProjectItem>();

		@Override
		public ProjectProxy getProject() {
			return project;
		}

		@Override
		public void setProject(ProjectProxy project) {
			this.project = (Project) project;
		}

		@Override
		public Set<ProjectItemProxy> getProjectItems() {
			return convertSet(projectItems, ProjectItemProxy.class);
		}

		@Override
		public void setProjectItems(Set<ProjectItemProxy> projectItems) {
			this.projectItems = castSet(projectItems, ProjectItem.class);
		}
	}

	public static class ProjectVO {
		private String name;
		private Set<ProjectItemVO> projectItems = new HashSet<ProjectItemVO>();

		public String toString() {
			return "<ProjectVO name=" + name + " items="
					+ projectItems.toString() + ">";
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Set<ProjectItemVO> getProjectItems() {
			return projectItems;
		}

		public void setProjectItems(Set<ProjectItemVO> projectItems) {
			this.projectItems = projectItems;
		}
	}

	public static class ProjectItemVO {
		private String name;

		private ProjectVO project;

		private Set<InvoiceItemVO> invoiceItems = new HashSet<InvoiceItemVO>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProjectVO getProject() {
			return project;
		}

		public void setProject(ProjectVO project) {
			this.project = project;
		}

		public Set<InvoiceItemVO> getInvoiceItems() {
			return invoiceItems;
		}

		// public void setInvoiceItems(Set<InvoiceItemVO> invoiceItems) {
		// this.invoiceItems = invoiceItems;
		// }
	}

	public static class InvoiceItemVO {

		private ProjectVO project;

		private Set<ProjectItemVO> projectItems = new HashSet<ProjectItemVO>();

		public ProjectVO getProject() {
			return project;
		}

		public void setProject(ProjectVO project) {
			this.project = project;
		}

		public Set<ProjectItemVO> getProjectItems() {
			return projectItems;
		}

		// public void setProjectItems(Set<ProjectItemVO> projectItems) {
		// this.projectItems = projectItems;
		// }
	}

	public static <T> Set<T> convertSet(Set<? extends T> sourceSet,
			Class<T> targetType) {
		if (sourceSet == null)
			return null;
		Set<T> set = new HashSet<T>();
		set.addAll(sourceSet);
		return set;
	}

	public static <T, E> Set<T> castSet(Set<E> sourceSet, Class<T> clazz) {
		if (sourceSet == null)
			return null;
		Set<T> set = new HashSet<T>();
		set.addAll((Collection<? extends T>) sourceSet);
		return set;
	}
}
